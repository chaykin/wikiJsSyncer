package ru.chaykin.wjss.calc.page;

import java.util.Map;

import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.RemotePage;
import ru.chaykin.wjss.utils.page.PageHashUtils;

public class PageChangesCalc extends ChangesCalc<LocalPage, RemotePage, IPage, PageChange> {
    public PageChangesCalc(Context context) {
	super(context);
    }

    @Override
    protected Map<Long, RemotePage> getRemoteResources() {
	return context.remotePages();
    }

    @Override
    protected Map<Long, LocalPage> getLocalResources() {
	return context.localPages();
    }

    @Override
    protected String md5ResourceHash(LocalPage localPage) {
	return PageHashUtils.md5PageHash(localPage);
    }

    @Override
    protected PageChange newResourceChange(LocalPage localPage, RemotePage remotePage) {
	return new PageChange(localPage, remotePage);
    }
}
