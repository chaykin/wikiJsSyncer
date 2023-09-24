package ru.chaykin.wjss.graphql.query;

import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.model.PageItem;
import ru.chaykin.wjss.graphql.model.PageListItem;

@RequiredArgsConstructor
public class PageQuery {
    private static final String PAGE_QUERY = "{pages{single(id: %s){id path hash content updatedAt}}}";

    private final ClientApi api;

    public PageItem fetchPage(int id) {
        String query = String.format(PAGE_QUERY, id);
	//noinspection uncheckeds
	return api.query(Type.class, query).data().pages().single();
    }

    private record Pages(PageItem single) {
    }

    private record Data(Pages pages) {
    }

    private record Type(Data data) {
    }
}
