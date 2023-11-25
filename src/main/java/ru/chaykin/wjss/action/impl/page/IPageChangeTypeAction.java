package ru.chaykin.wjss.action.impl.page;

import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.RemotePage;

public interface IPageChangeTypeAction extends IChangeTypeAction<LocalPage, RemotePage, IPage, PageChange> {
}
