package ru.chaykin.wjss;

import java.sql.SQLException;

import ru.chaykin.wjss.action.ChangeTypeActionFactory;
import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.context.ContextManager;

public class App {

    public static void main(String[] args) {
	var changes = new ContextManager().apply(context -> new ChangesCalc(context).calculateChanges());
	changes.forEach(pc -> new ContextManager().execute(context -> processChange(context, pc)));
    }

    private static void processChange(Context context, PageChange change) {
	System.out.println(change);

	try {
	    if (change.hasConflicts()) {
		throw new UnsupportedOperationException();
	    } else {
		var action = ChangeTypeActionFactory.create(change.getChanges().iterator().next());
		action.execute(context, change);
		context.connection().commit();
	    }
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
