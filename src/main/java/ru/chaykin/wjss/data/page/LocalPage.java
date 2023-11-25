package ru.chaykin.wjss.data.page;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.utils.page.PageManager;

public class LocalPage implements IPage, ILocalResource {
    private final PageManager pageManager;

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

    public LocalPage(PageManager pageManager, ResultSet rs) throws SQLException {
	this.pageManager = pageManager;

	id = rs.getLong("id");
	title = rs.getString("title");
	description = rs.getString("description");
	locale = rs.getString("locale");
	remotePath = rs.getString("remote_path");
	localPath = Path.of(rs.getString("local_path"));
	contentType = rs.getString("content_type");
	remoteUpdatedAt = rs.getLong("remote_update_at");
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
	if (exists()) {
	    try {
		return pageManager.readPageContent(getLocalPath());
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

    @Override
    public String toString() {
	return String.format("LocalPage[id=%s, contentType=%s, md5Hash=%s, localPath=%s]",
			getId(), getContentType(), getMd5Hash(), getLocalPath());
    }
}
