package ru.chaykin.wjss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.model.PageItem;
import ru.chaykin.wjss.graphql.model.PageListItem;
import ru.chaykin.wjss.graphql.query.PageListQuery;
import ru.chaykin.wjss.graphql.query.PageQuery;

import static java.nio.charset.StandardCharsets.UTF_8;

public class App {
    private static final Properties applicationProp = loadAppProp();

    public static void main(String[] args) {
	ClientApi api = new ClientApi();
	PageListQuery pliQuery = new PageListQuery(api);
	PageQuery pQuery = new PageQuery(api);
	pliQuery.fetchPages().forEach(pli -> {
	    writePageToFs(pli, pQuery.fetchPage(pli.id()));
	});

    }

    private static void writePageToFs(PageListItem pageListItem, PageItem pageItem) {
	String repoPath = applicationProp.getProperty("wiki.js.pages.repository");
	Path pagePath = Path.of(repoPath, pageItem.path() + ".md"); //TODO ext!

	try {
	    Files.createDirectories(pagePath.getParent());
	    try (FileOutputStream out = new FileOutputStream(pagePath.toFile())) {
		IOUtils.write(pageItem.content(), out, UTF_8);
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private static Properties loadAppProp() {
	try (InputStream in = App.class.getClassLoader().getResourceAsStream("application.properties")) {
	    Properties prop = new Properties();
	    prop.load(in);

	    return prop;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
}
