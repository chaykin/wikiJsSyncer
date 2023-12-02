package ru.chaykin.wjss;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import ru.chaykin.wjss.action.impl.asset.AssetChangeTypeActionFactory;
import ru.chaykin.wjss.action.impl.page.PageChangeTypeActionFactory;
import ru.chaykin.wjss.change.ChangesProcessor;
import ru.chaykin.wjss.change.IncomingChangesResolver;
import ru.chaykin.wjss.change.OutgoingChangesResolver;
import ru.chaykin.wjss.command.SyncCommand;
import ru.chaykin.wjss.conflict.ConflictResolverFactory;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.context.ContextManager;
import ru.chaykin.wjss.git.GitManager;
import ru.chaykin.wjss.option.AppOptions;

@Log4j2
@RequiredArgsConstructor
public class App {
    private final ConflictResolverFactory conflictResolverFactory;

    public static void main(String[] args) {
	try {
	    AppOptions options = AppOptions.getOptions();
	    JCommander jc = JCommander.newBuilder().addObject(options).build();
	    jc.parse(args);

	    if (options.isHelp()) {
		jc.usage();
	    } else {
		new ContextManager().execute(c -> new SyncCommand().execute(c));
	    }
	} catch (ParameterException e) {
	    System.out.println(e.getMessage());

	    e.usage();
	    System.exit(1);
	}
    }

    private void execute(Context context) {
	GitManager gitMan = GitManager.instance();
	try {
	    gitMan.commitLocalChanges();
	    gitMan.checkoutServer();

	    IncomingChangesResolver incResolver = new IncomingChangesResolver();
	    ChangesProcessor changeProcessor = new ChangesProcessor(context);

	    var localPages = context.localPages();
	    var localAssets = context.localAssets();

	    var serverPages = context.serverPages();
	    var serverAssets = context.serverAssets();

	    var incPageChanges = incResolver.resolveChanges(localPages, serverPages);
	    var incAssetChanges = incResolver.resolveChanges(localAssets, serverAssets);

	    changeProcessor.processChanges(incPageChanges, PageChangeTypeActionFactory::createIncoming);
	    changeProcessor.processChanges(incAssetChanges, AssetChangeTypeActionFactory::createIncoming);

	    gitMan.commitServerChanges();
	    MergeResult mergeResult = gitMan.mergeServerToLocal();
	    if (mergeResult.getMergeStatus().isSuccessful()) {
		gitMan.mergeLocalToServer();
		var headAffected = gitMan.getHeadAffectedFiles();

		OutgoingChangesResolver outResolver = new OutgoingChangesResolver();
		var outPageChanges = outResolver.resolveChanges(localPages, serverPages, headAffected);
		var outAssetChanges = outResolver.resolveChanges(localAssets, serverAssets, headAffected);

		changeProcessor.processChanges(outPageChanges, PageChangeTypeActionFactory::createOutgoing);
		changeProcessor.processChanges(outAssetChanges, AssetChangeTypeActionFactory::createOutgoing);

		gitMan.checkoutLocal();
	    } else {
		//TODO stop!
	    }
	} catch (GitAPIException | IOException e) {
	    throw new RuntimeException(e);
	}

	/*
	boolean success = new ContextManager().apply(
			context -> new PostponedConflictsProcessor(context).processConflicts());
	if (success) {
	    new PageChangesProcessor(conflictResolverFactory).processChanges();
	    new AssetChangesProcessor().processChanges();
	} else {
	    log.debug("There is unresolved conflicts. Skip synchronization");
	}
	 */
    }
}
