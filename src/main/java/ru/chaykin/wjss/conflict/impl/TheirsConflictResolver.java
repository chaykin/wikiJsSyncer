package ru.chaykin.wjss.conflict.impl;

import org.apache.commons.lang3.NotImplementedException;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.conflict.ConflictResolver;
import ru.chaykin.wjss.context.Context;

public class TheirsConflictResolver extends ConflictResolver {
    public TheirsConflictResolver(Context context) {
	super(context);
    }

    @Override
    public void resolve(PageChange pageChange) {
	//TODO
	throw new NotImplementedException();
    }
}
