package ru.chaykin.wjss.conflict.impl;

import ru.chaykin.wjss.action.impl.page.PageChangeTypeActionFactory;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.conflict.ConflictResolver;
import ru.chaykin.wjss.context.Context;

public class MineConflictResolver extends ConflictResolver {
    public MineConflictResolver(Context context) {
	super(context);
    }

    @Override
    public void resolve(PageChange pageChange) {
	IChangeTypeAction action = PageChangeTypeActionFactory.create(pageChange.getLocalChange());
	action.execute(context, pageChange);
    }
}
