package ru.chaykin.wjss;

import ru.chaykin.wjss.action.DownloadPageAction;
import ru.chaykin.wjss.db.DatabaseManager;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.PageListQuery;
import ru.chaykin.wjss.graphql.query.PageQuery;

public class App {

    public static void main(String[] args) {
	ClientApi api = new ClientApi();

	PageListQuery pliQuery = new PageListQuery(api);
	PageQuery pQuery = new PageQuery(api);
	new DatabaseManager().execute(c -> pliQuery
			.fetchPages()
			.forEach(pli -> new DownloadPageAction().execute(c, pli, pQuery.fetchPage(pli.id()))));
    }
}
