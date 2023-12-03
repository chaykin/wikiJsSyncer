package ru.chaykin.wjss.data.asset;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

import ru.chaykin.wjss.data.ILocalResource;

public class LocalAsset implements IAsset, ILocalResource {
    private final long id;
    private final long folderId;
    private final String serverPath;
    private final Path localPath;
    private final String contentType;
    private final long serverUpdatedAt;
    private final String md5Hash;

    public LocalAsset(ResultSet rs) throws SQLException {
	id = rs.getLong("id");
	folderId = rs.getLong("folderId");
	serverPath = rs.getString("server_path");
	localPath = Path.of(rs.getString("local_path"));
	contentType = rs.getString("content_type");
	serverUpdatedAt = rs.getLong("server_update_at");
	md5Hash = rs.getString("md5_hash");
    }

    @Override
    public long getId() {
	return id;
    }

    @Override
    public long getFolderId() {
	return folderId;
    }

    @Override
    public String getServerPath() {
	return serverPath;
    }

    @Override
    public Path getLocalPath() {
	return localPath;
    }

    @Override
    public String getContentType() {
	return contentType;
    }

    @Override
    public long getServerUpdatedAt() {
	return serverUpdatedAt;
    }

    @Override
    public String getMd5Hash() {
	return md5Hash;
    }

    @Override
    public InputStream getContent() throws FileNotFoundException {
	if (exists()) {
	    return new BufferedInputStream(new FileInputStream(getLocalPath().toFile()));
	}

	return null;
    }

    @Override
    public String toString() {
	return String.format("LocalAsset[id=%s, contentType=%s, md5Hash=%s, localPath=%s]",
			getId(), getContentType(), getMd5Hash(), getLocalPath());
    }
}
