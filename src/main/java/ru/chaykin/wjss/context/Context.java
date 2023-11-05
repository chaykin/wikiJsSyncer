package ru.chaykin.wjss.context;

import java.sql.Connection;
import java.util.Map;

import ru.chaykin.wjss.calc.LocalPageFetcher;
import ru.chaykin.wjss.calc.RemotePageFetcher;
import ru.chaykin.wjss.data.LocalPage;
import ru.chaykin.wjss.data.RemotePage;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.utils.PageManager;

public class Context {
    private final Connection connection;
    private final ClientApi api;

    private final LocalPageFetcher localPageFetcher;
    private final RemotePageFetcher remotePageFetcher;

    private final PageManager pageManager;

    private Map<Long, RemotePage> remotePages;
    private Map<Long, LocalPage> localPages;

    public Context(Connection connection, ClientApi api) {
	this(connection, api, new LocalPageFetcher(connection), new RemotePageFetcher(api));
    }

    public Context(Connection connection, ClientApi api,
		    LocalPageFetcher localPageFetcher, RemotePageFetcher remotePageFetcher) {
	this.connection = connection;
	this.api = api;
	this.localPageFetcher = localPageFetcher;
	this.remotePageFetcher = remotePageFetcher;

	pageManager = new PageManager(this);
    }

    public Connection connection() {
	return connection;
    }

    public ClientApi api() {
	return api;
    }

    public PageManager pageManager() {
	return pageManager;
    }

    public Map<Long, RemotePage> remotePages() {
	if (remotePages == null) {
	    remotePages = remotePageFetcher.fetch();
	}

	return remotePages;
    }

    public Map<Long, LocalPage> localPages() {
	if (localPages == null) {
	    localPages = localPageFetcher.fetch(pageManager());
	}

	return localPages;
    }
}
