package ru.chaykin.wjss.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class LocalPage implements IPage {
    private final long id;
    private final String title;
    private final String description;
    private final String locale;
    private final String remotePath;
    private final Path localPath;
    private final String contentType;
    private final long remoteUpdatedAt;
    private final String md5Hash;
    private final String tags;

    public LocalPage(ResultSet rs) throws SQLException {
	id = rs.getLong("id");
	title = rs.getString("title");
	description = rs.getString("description");
	locale = rs.getString("locale");
	remotePath = rs.getString("remote_path");
	localPath = Path.of(rs.getString("local_path"));
	contentType = rs.getString("content_type");
	remoteUpdatedAt = rs.getLong("remote_updated_at");
	md5Hash = rs.getString("md5_hash");
	tags = rs.getString("tags");
    }

    @Override
    public long getId() {
	return id;
    }

    @Override
    public String getTitle() {
	return title;
    }

    @Override
    public String description() {
	return description;
    }

    @Override
    public String getLocale() {
	return locale;
    }

    @Override
    public String getRemotePath() {
	return remotePath;
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
    public long getRemoteUpdatedAt() {
	return remoteUpdatedAt;
    }

    @Override
    public String getMd5Hash() {
	return md5Hash;
    }

    @Override
    public String getContent() {
	if (Files.exists(getLocalPath())) {
	    try {
		return Files.readString(getLocalPath());
	    } catch (IOException e) {
		throw new RuntimeException(e);
	    }
	}

	return null;
    }

    @Override
    public List<String> getTags() {
	return Arrays.asList(tags.split(","));
    }

    public boolean isExists() {
	return Files.exists(getLocalPath());
    }
}
