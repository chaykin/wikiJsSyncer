package ru.chaykin.wjss;

import java.sql.SQLException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.ChangeTypeActionFactory;
import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.conflict.ConflictResolverFactory;
import ru.chaykin.wjss.conflict.PostponedConflictsProcessor;
import ru.chaykin.wjss.context.Context;
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
	    var changes = new ContextManager().apply(context -> new ChangesCalc(context).calculateChanges());
	    changes.forEach(pc -> new ContextManager().execute(context -> processChange(context, pc)));
	} else {
	    log.debug("There is unresolved conflicts. Skip synchronization");
	}
    }

    private void processChange(Context context, PageChange change) {
	System.out.println(change);

	try {
	    if (change.hasConflicts()) {
		conflictResolverFactory.createResolver(context).resolve(change);
	    } else {
		var action = ChangeTypeActionFactory.create(change.getChange());
		action.execute(context, change);
	    }
	    context.connection().commit();
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
