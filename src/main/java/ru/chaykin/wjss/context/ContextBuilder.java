package ru.chaykin.wjss.context;

import java.sql.Connection;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.change.fetch.LocalAssetFetcher;
import ru.chaykin.wjss.change.fetch.RemoteAssetFetcher;
import ru.chaykin.wjss.change.fetch.LocalPageFetcher;
import ru.chaykin.wjss.change.fetch.RemotePageFetcher;
import ru.chaykin.wjss.graphql.api.ClientApi;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class ContextBuilder {
    private final Connection connection;
    private final ClientApi api;

    private LocalPageFetcher localPageFetcher;
    private RemotePageFetcher remotePageFetcher;

    private LocalAssetFetcher localAssetFetcher;
    private RemoteAssetFetcher remoteAssetFetcher;

    public ContextBuilder localPageFetcher(LocalPageFetcher localPageFetcher) {
	this.localPageFetcher = localPageFetcher;
	return this;
    }

    public ContextBuilder remotePageFetcher(RemotePageFetcher remotePageFetcher) {
	this.remotePageFetcher = remotePageFetcher;
	return this;
    }

    public ContextBuilder localAssetFetcher(LocalAssetFetcher localAssetFetcher) {
	this.localAssetFetcher = localAssetFetcher;
	return this;
    }

    public ContextBuilder remoteAssetFetcher(RemoteAssetFetcher remoteAssetFetcher) {
	this.remoteAssetFetcher = remoteAssetFetcher;
	return this;
    }

    public Context build() {
	var lpf = Optional.ofNullable(localPageFetcher).orElseGet(() -> new LocalPageFetcher(connection));
	var rpf = Optional.ofNullable(remotePageFetcher).orElseGet(() -> new RemotePageFetcher(api));

	var laf = Optional.ofNullable(localAssetFetcher).orElseGet(() -> new LocalAssetFetcher(connection));
	var raf = Optional.ofNullable(remoteAssetFetcher).orElseGet(() -> new RemoteAssetFetcher(api));

	return new Context(connection, api, lpf, rpf, laf, raf);
    }
}

