package ru.chaykin.wjss.change.fetch;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.page.ServerPage;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.PageListQuery;

import static java.util.function.Function.identity;

@RequiredArgsConstructor
public class RemotePageFetcher {
    private final ClientApi api;

    public Map<Long, ServerPage> fetch() {
	PageListQuery query = new PageListQuery(api);

	return query.fetchPages().stream()
			.map(p -> new ServerPage(api, p))
			.collect(Collectors.toMap(ServerPage::getId, identity()));
    }
}
