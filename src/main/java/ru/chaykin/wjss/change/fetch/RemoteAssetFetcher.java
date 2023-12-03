package ru.chaykin.wjss.change.fetch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.data.asset.ServerAsset;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.AssetFoldersQuery;
import ru.chaykin.wjss.graphql.query.AssetFoldersQuery.AssetFolder;
import ru.chaykin.wjss.graphql.query.AssetListQuery;

@RequiredArgsConstructor
public class RemoteAssetFetcher {
    private final ClientApi api;

    public Map<Long, ServerAsset> fetch() {
	return fetch(List.of(new AssetFolder(0, "")))
			.collect(Collectors.toMap(IAsset::getId, Function.identity()));
    }

    private Stream<ServerAsset> fetch(List<AssetFolder> folders) {
	long folderId = folders.getLast().id();

	AssetListQuery assetsQuery = new AssetListQuery(api);
	var assets = assetsQuery.fetchAssets(folderId).stream()
			.map(a -> new ServerAsset(api, a, folders));

	AssetFoldersQuery foldersQuery = new AssetFoldersQuery(api);
	var subAssets = foldersQuery.fetchAssetFolders(folderId).stream()
			.flatMap(f -> fetch(addSubFolder(folders, f)));

	return Stream.concat(assets, subAssets);
    }

    private List<AssetFolder> addSubFolder(List<AssetFolder> folders, AssetFolder folder) {
	List<AssetFolder> result = new ArrayList<>(folders);
	result.add(folder);

	return Collections.unmodifiableList(result);
    }
}
