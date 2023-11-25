package ru.chaykin.wjss.change;

import java.sql.SQLException;

import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.context.ContextManager;
import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.data.IRemoteResource;
import ru.chaykin.wjss.data.IResource;

public abstract class ChangesProcessor<L extends ILocalResource, R extends IRemoteResource,
		C extends IResource, RC extends ResourceChange<L, R, C>> {

    public void processChanges() {
	var changes = new ContextManager().apply(context -> createChangeCalc(context).calculateChanges());
	changes.forEach(rc -> new ContextManager().execute(context -> processChange(context, rc)));
    }

    protected abstract ChangesCalc<L, R, C, RC> createChangeCalc(Context context);

    protected abstract void resolveConflicts(Context context, RC change);

    protected abstract IChangeTypeAction<L, R, C, RC> createChangeTypeAction(ChangeType changeType);

    protected void processChange(Context context, RC change) {
	System.out.println(change);

	try {
	    if (change.hasConflicts()) {
		resolveConflicts(context, change);
	    } else {
		var action = createChangeTypeAction(change.getChange());
		action.execute(context, change);
	    }
	    context.connection().commit();
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
