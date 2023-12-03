package ru.chaykin.wjss.command;

import java.util.Collection;
import java.util.TreeSet;

import com.beust.jcommander.Parameters;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import ru.chaykin.wjss.git.GitManager;

@Log4j2
@Parameters(commandNames = "status", commandDescription = "Show the working copy status")
public class StatusCommand extends BaseCommand {
    private final GitManager gitMan = GitManager.instance();

    @Override
    public void execute() {
	try {
	    Status status = gitMan.getStatus();
	    if (status.isClean()) {
		System.out.println("There are no changes");
	    } else {
		printBlock(status.getConflicting(), "Unresolved conflicts:");
		printBlock(union(status.getChanged(), status.getModified()), "Updated files:");
		printBlock(union(status.getAdded(), status.getUntracked()), "New files:");
		printBlock(union(status.getRemoved(), status.getMissing()), "Removed files:");
	    }
	} catch (GitAPIException e) {
	    throw new RuntimeException(e);
	}
    }

    private Collection<String> union(Collection<String> c1, Collection<String> c2) {
	Collection<String> result = new TreeSet<>();
	result.addAll(c1);
	result.addAll(c2);

	return result;
    }

    private void printBlock(Collection<String> files, String msg) {
	if (!files.isEmpty()) {
	    System.out.println(msg);
	    files.forEach(f -> System.out.println("\t" + f));
	}
    }
}
