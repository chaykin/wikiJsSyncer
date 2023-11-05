package ru.chaykin.wjss.calc;

import java.util.Collections;
import java.util.Date;

import ru.chaykin.wjss.data.RemotePage;
import ru.chaykin.wjss.graphql.query.PageListQuery;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class RemotePageFactory {
    public static final String LOCALE = "ru";

    public static RemotePage createRemotePage(long id, String path, Date updatedAt, String content) {
	PageListQuery.PageListItem pli = new PageListQuery.PageListItem(id, path, LOCALE, "testTitle", "testDescr", "markdown", updatedAt,
			Collections.emptyList());

	RemotePage page = spy(new RemotePage(null, pli));
	doReturn(content).when(page).getContent();
	return page;
    }
}
