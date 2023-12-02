package ru.chaykin.wjss.change.page;

import ru.chaykin.wjss.change.ResourceChange;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.ServerPage;

public class PageChange extends ResourceChange<LocalPage, ServerPage, IPage> {

    public PageChange(LocalPage localPage, ServerPage remotePage) {
	super(localPage, remotePage);
    }
}
