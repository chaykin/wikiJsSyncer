package ru.chaykin.wjss.action;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.config.ApplicationConfig;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.graphql.model.PageItem;
import ru.chaykin.wjss.graphql.model.PageListItem;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DownloadPageAction {
    private static final String INSERT_PAGE_QUERY = """
		INSERT OR REPLACE INTO pages(
				id, title, locale,
				remote_path, local_path,
				remote_update_at, local_update_at,
				content_type, tags)
			VALUES(?,?,?,?,?,?,?,?,?)""";

    public void execute(Connection connection, PageListItem pageListItem, PageItem pageItem) {
	String repoPath = ApplicationConfig.get("wiki.js.pages.repository");
	Path pagePath = Path.of(repoPath, pageItem.path() + ".md"); //TODO ext!

	try {
	    Files.createDirectories(pagePath.getParent());
	    try (FileOutputStream out = new FileOutputStream(pagePath.toFile())) {
		IOUtils.write(pageItem.content(), out, UTF_8);
	    }

	    DatabaseUtils.update(connection, INSERT_PAGE_QUERY,
			    pageItem.id(),
			    pageListItem.title(),
			    pageListItem.locale(),
			    pageItem.path(),
			    pagePath.toString(),
			    pageItem.updatedAt().getTime(),
			    Files.getLastModifiedTime(pagePath).toMillis(),
			    pageListItem.contentType(),
			    StringUtils.join(pageListItem.tags(), ","));
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
