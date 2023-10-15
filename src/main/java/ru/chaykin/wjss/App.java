package ru.chaykin.wjss;

import ru.chaykin.wjss.action.ChangeTypeActionFactory;
import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.context.ContextManager;

public class App {

    public static void main(String[] args) {
	new ContextManager().execute(
			context -> new ChangesCalc(context).calculateChanges().forEach(c -> processChange(context, c)));
    }

    private static void processChange(Context context, PageChange change) {
	System.out.println(change);

	if (change.hasConflicts()) {
	    throw new UnsupportedOperationException();
	} else {
	    var action = ChangeTypeActionFactory.create(change.getChanges().iterator().next());
	    action.execute(context, change.getPage());
	}
    }
}
