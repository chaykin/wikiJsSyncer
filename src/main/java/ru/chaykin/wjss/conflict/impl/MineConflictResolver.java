package ru.chaykin.wjss.conflict.impl;

import ru.chaykin.wjss.action.ChangeTypeActionFactory;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.conflict.ConflictResolver;
import ru.chaykin.wjss.context.Context;

public class MineConflictResolver extends ConflictResolver {
    public MineConflictResolver(Context context) {
	super(context);
    }

    @Override
    public void resolve(PageChange pageChange) {
	IChangeTypeAction action = ChangeTypeActionFactory.create(pageChange.getLocalChange());
	action.execute(context, pageChange);
    }
}
