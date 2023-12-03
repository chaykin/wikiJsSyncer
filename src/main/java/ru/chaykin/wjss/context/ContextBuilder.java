package ru.chaykin.wjss.context;

import java.sql.Connection;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.change.fetch.LocalAssetFetcher;
import ru.chaykin.wjss.change.fetch.ServerAssetFetcher;
import ru.chaykin.wjss.change.fetch.LocalPageFetcher;
import ru.chaykin.wjss.change.fetch.ServerPageFetcher;
import ru.chaykin.wjss.graphql.api.ClientApi;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class ContextBuilder {
    private final Connection connection;
    private final ClientApi api;

    private LocalPageFetcher localPageFetcher;
    private ServerPageFetcher serverPageFetcher;

    private LocalAssetFetcher localAssetFetcher;
    private ServerAssetFetcher serverAssetFetcher;

    public ContextBuilder localPageFetcher(LocalPageFetcher localPageFetcher) {
	this.localPageFetcher = localPageFetcher;
	return this;
    }

    public ContextBuilder serverPageFetcher(ServerPageFetcher serverPageFetcher) {
	this.serverPageFetcher = serverPageFetcher;
	return this;
    }

    public ContextBuilder localAssetFetcher(LocalAssetFetcher localAssetFetcher) {
	this.localAssetFetcher = localAssetFetcher;
	return this;
    }

    public ContextBuilder serverAssetFetcher(ServerAssetFetcher serverAssetFetcher) {
	this.serverAssetFetcher = serverAssetFetcher;
	return this;
    }

    public Context build() {
	var lpf = Optional.ofNullable(localPageFetcher).orElseGet(() -> new LocalPageFetcher(connection));
	var rpf = Optional.ofNullable(serverPageFetcher).orElseGet(() -> new ServerPageFetcher(api));

	var laf = Optional.ofNullable(localAssetFetcher).orElseGet(() -> new LocalAssetFetcher(connection));
	var raf = Optional.ofNullable(serverAssetFetcher).orElseGet(() -> new ServerAssetFetcher(api));

	return new Context(connection, api, lpf, rpf, laf, raf);
    }
}

