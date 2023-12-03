package ru.chaykin.wjss.change;

import java.util.Collections;
import java.util.Date;

import ru.chaykin.wjss.data.page.ServerPage;
import ru.chaykin.wjss.graphql.query.PageListQuery;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class ServerPageFactory {
    public static final String LOCALE = "ru";

    public static ServerPage createServerPage(long id, String path, Date updatedAt, String content) {
	PageListQuery.PageListItem pli = new PageListQuery.PageListItem(id, path, LOCALE, "testTitle", "testDescr", "markdown", updatedAt,
			Collections.emptyList());

	ServerPage page = spy(new ServerPage(null, pli));
	doReturn(content).when(page).getContent();
	return page;
    }
}
