package ru.chaykin.wjss.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand.Stage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.MergeCommand.FastForwardMode;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import ru.chaykin.wjss.config.ApplicationConfig;

import static org.eclipse.jgit.api.ResetCommand.ResetType.HARD;

@Log4j2
public class GitManager {
    public static final String REPO_PATH = ApplicationConfig.get("wiki.js.repo");

    private static final String LOCAL_BRANCH_NAME = "main";
    private static final String SERVER_BRANCH_NAME = "server";

    private static final GitManager INSTANCE = new GitManager();

    private final Git git;

    private GitManager() {
	try {
	    InitCommand initCmd = Git.init().setDirectory(getRepoFile()).setInitialBranch(LOCAL_BRANCH_NAME);
	    git = initCmd.call();

	    if (git.branchList().call().isEmpty()) {
		log.debug("Creating Git repo...");

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

    public void resolveConflictAsOurs(List<String> paths) throws GitAPIException {
	resolveConflict(paths, Stage.OURS);
    }

    public void resolveConflictAsTheirs(List<String> paths) throws GitAPIException {
	resolveConflict(paths, Stage.THEIRS);
    }

    public void markAsResolved(Collection<String> paths) throws GitAPIException {
	log.debug("Mark conflict resolved");

	paths.stream().reduce(git.add(), AddCommand::addFilepattern, (c1, c2) -> c1).call();
    }

    public Set<String> getConflicts() throws GitAPIException {
	return getStatus().getConflicting();
    }

    public Status getStatus() throws GitAPIException {
	return git.status().call();
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
	log.debug("Checking out to branch: {}", branchName);

	git.checkout().setName(branchName).call();
    }

    private void commitChanges(String branchName) throws GitAPIException {
	checkout(branchName);

	git.add().addFilepattern(".").call();

	Collection<String> removed = git.status().call().getMissing();
	if (!removed.isEmpty()) {
	    removed.stream().reduce(git.rm(), RmCommand::addFilepattern, (c1, c2) -> c1).call();
	}

	log.debug("Commiting to branch: {}", branchName);
	git.commit().setMessage("Commit %s changes".formatted(branchName)).call();
    }

    private MergeResult merge(String srcBranchName, String tgtBranchName) throws GitAPIException, IOException {
	log.debug("Merging branches: {} -> {}", srcBranchName, tgtBranchName);

	checkout(tgtBranchName);
	ObjectId mergeBase = git.getRepository().resolve(srcBranchName);

	return git.merge()
			.include(mergeBase)
			.setMessage("Merge %s -> %s".formatted(srcBranchName, tgtBranchName))
			.setFastForward(FastForwardMode.NO_FF)
			.call();
    }

    private void resolveConflict(List<String> paths, Stage stage) throws GitAPIException {
	log.debug("Resoling conflict as: {}", stage);

	git.checkout().setStage(stage).addPaths(paths).call();
	markAsResolved(paths);
    }

    private File getRepoFile() throws IOException {
	Path repoPath = Path.of(REPO_PATH);
	Files.createDirectories(repoPath);
	return repoPath.toFile();
    }
}
