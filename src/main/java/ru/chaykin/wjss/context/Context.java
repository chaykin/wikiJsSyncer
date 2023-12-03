package ru.chaykin.wjss.context;

import java.sql.Connection;
import java.util.Map;

import ru.chaykin.wjss.change.fetch.LocalAssetFetcher;
import ru.chaykin.wjss.change.fetch.RemoteAssetFetcher;
import ru.chaykin.wjss.change.fetch.LocalPageFetcher;
import ru.chaykin.wjss.change.fetch.RemotePageFetcher;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.ServerAsset;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.ServerPage;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.utils.page.PageManager;

public class Context {
    private final Connection connection;
    private final ClientApi api;

    private final LocalPageFetcher localPageFetcher;
    private final RemotePageFetcher remotePageFetcher;

    private final LocalAssetFetcher localAssetFetcher;
    private final RemoteAssetFetcher remoteAssetFetcher;

    private final PageManager pageManager;

    private Map<Long, ServerPage> remotePages;
    private Map<Long, LocalPage> localPages;

    private Map<Long, ServerAsset> remoteAssets;
    private Map<Long, LocalAsset> localAssets;

    public Context(Connection connection, ClientApi api,
		    LocalPageFetcher localPageFetcher, RemotePageFetcher remotePageFetcher,
		    LocalAssetFetcher localAssetFetcher, RemoteAssetFetcher remoteAssetFetcher) {
	this.connection = connection;
	this.api = api;
	this.localPageFetcher = localPageFetcher;
	this.remotePageFetcher = remotePageFetcher;
	this.localAssetFetcher = localAssetFetcher;
	this.remoteAssetFetcher = remoteAssetFetcher;

	pageManager = new PageManager(this);
    }

    public static ContextBuilder createBuilder(Connection connection, ClientApi api) {
	return new ContextBuilder(connection, api);
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

    public Map<Long, ServerPage> serverPages() {
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

    public Map<Long, ServerAsset> serverAssets() {
	if (remoteAssets == null) {
	    remoteAssets = remoteAssetFetcher.fetch();
	}

	return remoteAssets;
    }

    public Map<Long, LocalAsset> localAssets() {
	if (localAssets == null) {
	    localAssets = localAssetFetcher.fetch();
	}

	return localAssets;
    }
}
