package ru.chaykin.wjss.graphql.query;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.graphql.api.ClientApi;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@RequiredArgsConstructor
public class PageQuery {
    private static final String PAGE_QUERY = "{pages{single(id: %s){id content updatedAt}}}";

    private final ClientApi api;

    public PageItem fetchPage(long id) {
	String query = String.format(PAGE_QUERY, id);
	//noinspection uncheckeds
	return api.query(Type.class, query).data().pages().single();
    }

    @JsonInclude(NON_NULL)
    public record PageItem(long id, String content, Date updatedAt) {
    }

    private record Pages(PageItem single) {
    }

    private record Data(Pages pages) {
    }

    private record Type(Data data) {
    }
}
