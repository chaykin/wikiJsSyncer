package ru.chaykin.wjss.graphql.query;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.model.PageListItem;

@RequiredArgsConstructor
public class PageListQuery {
    private static final String PAGES_QUERY = "{pages{list(orderBy: TITLE){id path locale title description contentType updatedAt tags}}}";

    private final ClientApi api;

    public List<PageListItem> fetchPages() {
	//noinspection uncheckeds
	return api.query(Type.class, PAGES_QUERY).data().pages().list();
    }

    private record Pages(List<PageListItem> list) {
    }

    private record Data(Pages pages) {
    }

    private record Type(Data data) {
    }
}
