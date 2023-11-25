package ru.chaykin.wjss.data.asset;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.config.ApplicationConfig;
import ru.chaykin.wjss.data.IRemoteResource;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.AssetFoldersQuery.AssetFolder;
import ru.chaykin.wjss.graphql.query.AssetListQuery.Asset;
import ru.chaykin.wjss.utils.asset.AssetHashUtils;

@RequiredArgsConstructor
public class RemoteAsset implements IAsset, IRemoteResource {
    private final ClientApi api;
    private final Asset asset;
    private final List<AssetFolder> folders;

    private boolean isContentDownloaded;
    private String md5Hash;

    @Override
    public long getFolderId() {
	return folders.getLast().id();
    }

    @Override
    public InputStream getContent() throws IOException {
	File tmpFile = getTmpContentFile();
	if (!isContentDownloaded) {
	    api.downloadAsset(getRemotePath(), tmpFile);
	    isContentDownloaded = true;
	}

	return new BufferedInputStream(new FileInputStream(tmpFile));
    }

    @Override
    public long getId() {
	return asset.id();
    }

    @Override
    public String getRemotePath() {
	String folderPath = folders.stream().map(AssetFolder::name).collect(Collectors.joining("/", "", "/"));
	return folderPath + asset.filename();
    }

    @Override
    public Path getLocalPath() {
	String repoPath = ApplicationConfig.get("wiki.js.assets.repository");
	return Path.of(repoPath, getRemotePath());
    }

    @Override
    public String getContentType() {
	return asset.mime();
    }

    @Override
    public String getMd5Hash() {
	if (md5Hash == null) {
	    md5Hash = AssetHashUtils.md5PageHash(this);
	}

	return md5Hash;
    }

    @Override
    public long getRemoteUpdatedAt() {
	return asset.updatedAt().getTime();
    }

    @Override
    public String toString() {
	return String.format("RemoteAsset[id=%s, contentType=%s, md5Hash=%s, remotePath=%s]",
			getId(), getContentType(), getMd5Hash(), getRemotePath());
    }

    private File getTmpContentFile() throws IOException {
	Path baseTmp = Path.of(ApplicationConfig.get("wiki.js.temp"));
	Files.createDirectories(baseTmp);

	String name = getId() + asset.ext();
	File file = new File(baseTmp.toFile(), name);
	file.deleteOnExit();

	return file;
    }
}
