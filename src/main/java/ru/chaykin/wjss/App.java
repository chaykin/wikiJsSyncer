package ru.chaykin.wjss;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.change.asset.AssetChangesProcessor;
import ru.chaykin.wjss.change.page.PageChangesProcessor;
import ru.chaykin.wjss.conflict.ConflictResolverFactory;
import ru.chaykin.wjss.conflict.PostponedConflictsProcessor;
import ru.chaykin.wjss.context.ContextManager;
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
		new App(new ConflictResolverFactory()).execute();
	    }
	} catch (ParameterException e) {
	    System.out.println(e.getMessage());

	    e.usage();
	    System.exit(1);
	}
    }

    private void execute() {
	boolean success = new ContextManager().apply(
			context -> new PostponedConflictsProcessor(context).processConflicts());
	if (success) {
	    new PageChangesProcessor(conflictResolverFactory).processChanges();
	    new AssetChangesProcessor().processChanges();
	} else {
	    log.debug("There is unresolved conflicts. Skip synchronization");
	}
    }
}
