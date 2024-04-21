package ru.chaykin.wjss.action;

import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IResource;

public abstract class ChangeTypeAction {
    private boolean success = true;

    public boolean execute(Context context, Long id) {
	doExecute(context, id);
	return success;
    }

    protected abstract void doExecute(Context context, Long id);

    public abstract IResource actionResource(Context context, Long id);

    protected void markAsFailed() {
	success = false;
    }
}
