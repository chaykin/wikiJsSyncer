package ru.chaykin.wjss.command;

import java.io.IOException;

import com.beust.jcommander.Parameters;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import ru.chaykin.wjss.action.impl.asset.AssetChangeTypeActionFactory;
import ru.chaykin.wjss.action.impl.page.PageChangeTypeActionFactory;
import ru.chaykin.wjss.change.ChangesProcessor;
import ru.chaykin.wjss.change.IncomingChangesResolver;
import ru.chaykin.wjss.change.OutgoingChangesResolver;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.git.GitManager;
import ru.chaykin.wjss.utils.ExceptionUtils;

@Log4j2
@Parameters(commandDescription = "Run synchronization with Wiki.js server")
public class SyncCommand {
    private final GitManager gitMan = GitManager.instance();

    public void execute(Context context) {
	try {
	    doExecute(context);
	} catch (GitAPIException | IOException e) {
	    throw new RuntimeException("Could not execute sync command", e);
	}
    }

    public void doExecute(Context context) throws GitAPIException, IOException {
	gitMan.checkoutLocal();
	if (hasConflicts()) {
	    return;
	}

	gitMan.commitLocalChanges();

	gitMan.checkoutServer();
	ExceptionUtils.tryFinally(() -> processIncomingChanges(context), gitMan::commitServerChanges);

	MergeResult mergeResult = gitMan.mergeServerToLocal();
	if (mergeResult.getMergeStatus().isSuccessful()) {
	    gitMan.mergeLocalToServer();
	    ExceptionUtils.tryFinally(() -> {
		try {
		    processOutgoingChanges(context);
		} catch (Throwable t) {
		    try {
			gitMan.resetHeadCommit();
		    } catch (Throwable innerT) {
			t.addSuppressed(innerT);
		    }

		    throw t;
		}
	    }, gitMan::checkoutLocal);
	} else {
	    var conflicts = mergeResult.getConflicts().keySet();
	    System.out.println("There are unresolved conflicts:");
	    conflicts.forEach(c -> System.out.println("\t" + c));

	    log.debug("There are unresolved conflicts ({}). Stop synchronization", conflicts);
	}
    }

    private boolean hasConflicts() throws GitAPIException {
	var conflicts = gitMan.getConflicts();
	if (conflicts.isEmpty()) {
	    return false;
	}

	System.out.println("There are unresolved conflicts: ");
	conflicts.forEach(c -> System.out.println("\t" + c));

	log.debug("There are unresolved conflicts ({}). Skip synchronization", conflicts);
	return true;
    }

    private void processIncomingChanges(Context context) {
	IncomingChangesResolver incResolver = new IncomingChangesResolver();
	ChangesProcessor changeProcessor = new ChangesProcessor(context);

	var incPageChanges = incResolver.resolveChanges(context.localPages(), context.serverPages());
	var incAssetChanges = incResolver.resolveChanges(context.localAssets(), context.serverAssets());

	changeProcessor.processChanges(incPageChanges, PageChangeTypeActionFactory::createIncoming);
	changeProcessor.processChanges(incAssetChanges, AssetChangeTypeActionFactory::createIncoming);
    }

    private void processOutgoingChanges(Context context) throws GitAPIException, IOException {
	var headAffected = gitMan.getHeadAffectedFiles();

	OutgoingChangesResolver outResolver = new OutgoingChangesResolver();
	ChangesProcessor changeProcessor = new ChangesProcessor(context);

	var outPageChanges = outResolver.resolveChanges(context.localPages(), context.serverPages(), headAffected);
	var outAssetChanges = outResolver.resolveChanges(context.localAssets(), context.serverAssets(), headAffected);

	changeProcessor.processChanges(outPageChanges, PageChangeTypeActionFactory::createOutgoing);
	changeProcessor.processChanges(outAssetChanges, AssetChangeTypeActionFactory::createOutgoing);
    }
}
