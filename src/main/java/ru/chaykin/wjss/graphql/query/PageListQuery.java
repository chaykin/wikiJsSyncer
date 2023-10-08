package ru.chaykin.wjss.graphql.query;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.graphql.api.ClientApi;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@RequiredArgsConstructor
public class PageListQuery {
    private static final String PAGES_QUERY = "{pages{list(orderBy: TITLE){id path locale title description contentType updatedAt tags}}}";

    private final ClientApi api;

    public List<PageListItem> fetchPages() {
	//noinspection uncheckeds
	return api.query(Type.class, PAGES_QUERY).data().pages().list();
    }

    @JsonInclude(NON_NULL)
    public record PageListItem(long id, String path, String locale, String title, String description,
			       String contentType, Date updatedAt, List<String> tags) {
    }

    private record Pages(List<PageListItem> list) {
    }

    private record Data(Pages pages) {
    }

    private record Type(Data data) {
    }
}
