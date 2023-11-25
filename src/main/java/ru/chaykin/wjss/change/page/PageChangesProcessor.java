package ru.chaykin.wjss.change.page;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.PageChangeTypeActionFactory;
import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.calc.page.PageChangesCalc;
import ru.chaykin.wjss.change.ChangeType;
import ru.chaykin.wjss.change.ChangesProcessor;
import ru.chaykin.wjss.conflict.ConflictResolverFactory;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.RemotePage;

@RequiredArgsConstructor
public class PageChangesProcessor extends ChangesProcessor<LocalPage, RemotePage, IPage, PageChange> {
    private final ConflictResolverFactory conflictResolverFactory;

    @Override
    protected ChangesCalc<LocalPage, RemotePage, IPage, PageChange> createChangeCalc(Context context) {
	return new PageChangesCalc(context);
    }

    @Override
    protected void resolveConflicts(Context context, PageChange change) {
	conflictResolverFactory.createResolver(context).resolve(change);
    }

    @Override
    protected IChangeTypeAction<LocalPage, RemotePage, IPage, PageChange> createChangeTypeAction(
		    ChangeType changeType) {
	return PageChangeTypeActionFactory.create(changeType);
    }
}
