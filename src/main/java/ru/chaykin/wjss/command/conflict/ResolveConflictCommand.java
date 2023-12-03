package ru.chaykin.wjss.command.conflict;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.errors.GitAPIException;
import ru.chaykin.wjss.command.BaseCommand;
import ru.chaykin.wjss.git.GitManager;
import ru.chaykin.wjss.option.ResolveTypeConverter;
import ru.chaykin.wjss.option.ResolveTypeValidator;

@Log4j2
@Parameters(commandNames = "resolve", commandDescription = "Resolve synchronization conflicts")
public class ResolveConflictCommand extends BaseCommand {
    private final GitManager gitMan = GitManager.instance();

    @Parameter(description = "File paths to resolve conflicts", required = true)
    private List<String> paths;

    @Parameter(
		    names = { "-s", "--strategy" },
		    required = true,
		    description = "Conflict resolution strategy: (m)anual, (o)urs, (t)heirs",
		    validateWith = ResolveTypeValidator.class,
		    converter = ResolveTypeConverter.class)
    private ResolveType resolveType;

    @Override
    public void execute() {
	try {
	    var pathsToResolve = new ArrayList<>(paths);
	    pathsToResolve.retainAll(gitMan.getConflicts());

	    if (pathsToResolve.isEmpty()) {
		System.out.println("No conflict file(s) specified");
		return;
	    }

	    switch (resolveType) {
	    case MANUAL -> gitMan.markAsResolved(pathsToResolve);
	    case OURS -> gitMan.resolveConflictAsOurs(pathsToResolve);
	    case THEIRS -> gitMan.resolveConflictAsTheirs(pathsToResolve);
	    }
	} catch (GitAPIException e) {
	    throw new RuntimeException(e);
	}
    }
}
