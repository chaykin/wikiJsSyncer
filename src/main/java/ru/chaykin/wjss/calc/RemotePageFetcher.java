package ru.chaykin.wjss.calc;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.RemotePage;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.PageListQuery;

import static java.util.function.Function.identity;

@RequiredArgsConstructor
public class RemotePageFetcher {
    private final ClientApi api;

    public Map<Long, RemotePage> fetch() {
	PageListQuery query = new PageListQuery(api);

	return query.fetchPages().stream()
			.map(p -> new RemotePage(api, p))
			.collect(Collectors.toMap(RemotePage::getId, identity()));
    }
}
