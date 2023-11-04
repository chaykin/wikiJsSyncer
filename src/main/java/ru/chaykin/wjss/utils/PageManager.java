package ru.chaykin.wjss.utils;

import java.nio.file.Path;

import ru.chaykin.wjss.config.ApplicationConfig;

public class PageManager {
    private PageManager() {
    }

    public static Path toLocalPath(String type, String locale, String remotePath) {
	String repoPath = ApplicationConfig.get("wiki.js.pages.repository");
	String extension = PageContentType.of(type).getExtension();

	return Path.of(repoPath, locale, String.format("%s.%s", remotePath, extension));
    }
}
