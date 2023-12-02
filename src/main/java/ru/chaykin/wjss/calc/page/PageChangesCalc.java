package ru.chaykin.wjss.calc.page;

import java.util.Map;

import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.ServerPage;
import ru.chaykin.wjss.utils.page.PageHashUtils;

public class PageChangesCalc extends ChangesCalc<LocalPage, ServerPage, IPage, PageChange> {
    public PageChangesCalc(Context context) {
	super(context);
    }

    @Override
    protected Map<Long, ServerPage> getRemoteResources() {
	return context.serverPages();
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
    protected PageChange newResourceChange(LocalPage localPage, ServerPage remotePage) {
	return new PageChange(localPage, remotePage);
    }
}
