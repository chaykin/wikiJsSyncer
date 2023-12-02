package ru.chaykin.wjss.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.MergeCommand.FastForwardMode;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import ru.chaykin.wjss.utils.PathUtils;

import static org.eclipse.jgit.api.ResetCommand.ResetType.HARD;

@Log4j2 //TODO logging!
public class GitManager {
    private static final String LOCAL_BRANCH_NAME = "main";
    private static final String SERVER_BRANCH_NAME = "server";
    private static final Path REPO_PATH = Path.of(PathUtils.REPO_PATH);

    private static final GitManager INSTANCE = new GitManager();

    private final Git git;

    private GitManager() {
	try {
	    InitCommand initCmd = Git.init().setDirectory(getRepoFile()).setInitialBranch(LOCAL_BRANCH_NAME);
	    git = initCmd.call();

	    if (git.branchList().call().isEmpty()) {
		git.commit().setMessage("Initial commit").call();
		git.branchCreate().setName(SERVER_BRANCH_NAME).call();
	    }
	} catch (IOException | GitAPIException e) {
	    throw new RuntimeException(e);
	}
    }

    public static GitManager instance() {
	return INSTANCE;
    }

    public void checkoutLocal() throws GitAPIException {
	checkout(LOCAL_BRANCH_NAME);
    }

    public void checkoutServer() throws GitAPIException {
	checkout(SERVER_BRANCH_NAME);
    }

    public void commitLocalChanges() throws GitAPIException {
	commitChanges(LOCAL_BRANCH_NAME);
    }

    public void commitServerChanges() throws GitAPIException {
	commitChanges(SERVER_BRANCH_NAME);
    }

    public MergeResult mergeServerToLocal() throws GitAPIException, IOException {
	return merge(SERVER_BRANCH_NAME, LOCAL_BRANCH_NAME);
    }

    public MergeResult mergeLocalToServer() throws GitAPIException, IOException {
	return merge(LOCAL_BRANCH_NAME, SERVER_BRANCH_NAME);
    }

    public Set<String> getConflicts() throws GitAPIException {
	return git.status().call().getConflicting();
    }

    public Collection<DiffEntry> getHeadAffectedFiles() throws GitAPIException, IOException {
	RevCommit head = headCommit();
	RevCommit base = head.getParent(0);

	ObjectId headId = head.getTree().getId();
	ObjectId baseId = base.getTree().getId();

	TreeWalk walk = new TreeWalk(git.getRepository());
	walk.setRecursive(true);
	walk.setFilter(TreeFilter.ANY_DIFF);
	walk.reset(baseId, headId);

	return DiffEntry.scan(walk);
    }

    public void resetHeadCommit() throws GitAPIException {
	git.reset().setMode(HARD).call();
    }

    private RevCommit headCommit() throws GitAPIException {
	return git.log().setMaxCount(1).call().iterator().next();
    }

    private void checkout(String branchName) throws GitAPIException {
	git.checkout().setName(branchName).call();
    }

    private void commitChanges(String branchName) throws GitAPIException {
	checkout(branchName);

	git.add().addFilepattern(".").call();

	Collection<String> removed = git.status().call().getMissing();
	if (!removed.isEmpty()) {
	    removed.stream().reduce(git.rm(), RmCommand::addFilepattern, (c1, c2) -> c1).call();
	}

	git.commit().setMessage("Commit %s changes".formatted(branchName)).call();
    }

    private MergeResult merge(String srcBranchName, String tgtBranchName) throws GitAPIException, IOException {
	checkout(tgtBranchName);
	ObjectId mergeBase = git.getRepository().resolve(srcBranchName);

	return git.merge()
			.include(mergeBase)
			.setMessage("Merge %s -> %s".formatted(srcBranchName, tgtBranchName))
			.setFastForward(FastForwardMode.NO_FF)
			.call();
    }

    private File getRepoFile() throws IOException {
	Files.createDirectories(REPO_PATH);
	return REPO_PATH.toFile();
    }
}
