package ru.chaykin.wjss.change.page;

import ru.chaykin.wjss.change.ResourceChange;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.RemotePage;

public class PageChange extends ResourceChange<LocalPage, RemotePage, IPage> {

    public PageChange(LocalPage localPage, RemotePage remotePage) {
	super(localPage, remotePage);
    }
}
