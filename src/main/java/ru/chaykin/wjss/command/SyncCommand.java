package ru.chaykin.wjss.command;

import java.io.IOException;
import java.sql.SQLException;

import com.beust.jcommander.Parameters;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import ru.chaykin.wjss.action.impl.asset.AssetChangeTypeActionFactory;
import ru.chaykin.wjss.action.impl.page.PageChangeTypeActionFactory;
import ru.chaykin.wjss.change.ChangesProcessor;
import ru.chaykin.wjss.change.IncomingChangesResolver;
import ru.chaykin.wjss.change.OutgoingChangesResolver;
import ru.chaykin.wjss.change.queue.OutgoingChangesQueueManager;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.context.ContextManager;
import ru.chaykin.wjss.git.GitManager;
import ru.chaykin.wjss.utils.ExceptionUtils;

@Log4j2
@Parameters(commandNames = "sync", commandDescription = "Run synchronization with Wiki.js server")
public class SyncCommand extends BaseCommand {
    private static final String INDENT = "  ";

    private final GitManager gitMan = GitManager.instance();

    @Override
    public void execute() {
	new ContextManager().execute(context -> {
	    try {
		System.out.println("Collect changes...");
		doExecute(context);
	    } catch (GitAPIException | IOException e) {
		throw new RuntimeException("Could not execute sync command", e);
	    }
	});
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
	    ExceptionUtils.tryFinally(() -> processOutgoingChanges(context), gitMan::checkoutLocal);
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
	System.out.println("Processing incoming changes...");

	IncomingChangesResolver incResolver = new IncomingChangesResolver();
	ChangesProcessor changeProcessor = new ChangesProcessor(context, INDENT);

	var incPageChanges = incResolver.resolveChanges(context.localPages(), context.serverPages());
	var incAssetChanges = incResolver.resolveChanges(context.localAssets(), context.serverAssets());

	boolean hasChanges = false;
	hasChanges |= changeProcessor.processChanges(incPageChanges, PageChangeTypeActionFactory::createIncoming);
	hasChanges |= changeProcessor.processChanges(incAssetChanges, AssetChangeTypeActionFactory::createIncoming);
	if (!hasChanges) {
	    System.out.printf("%sUP-TO-DATE%n", INDENT);
	}
    }

    private void processOutgoingChanges(Context context) throws GitAPIException, IOException, SQLException {
	System.out.println("Processing outgoing changes...");

	OutgoingChangesQueueManager queueMan = new OutgoingChangesQueueManager(context);
	var affected = queueMan.collectChanges();

	OutgoingChangesResolver outResolver = new OutgoingChangesResolver();
	ChangesProcessor changeProcessor = new ChangesProcessor(context, INDENT);

	var outPageChanges = outResolver.resolveChanges(context.localPages(), context.serverPages(), affected);
	var outAssetChanges = outResolver.resolveChanges(context.localAssets(), context.serverAssets(), affected);

	boolean hasChanges = false;
	hasChanges |= changeProcessor.processChanges(outPageChanges, PageChangeTypeActionFactory::createOutgoing);
	hasChanges |= changeProcessor.processChanges(outAssetChanges, AssetChangeTypeActionFactory::createOutgoing);
	if (!hasChanges) {
	    System.out.printf("%sUP-TO-DATE%n", INDENT);
	}

	queueMan.clearQueue();
    }
}
