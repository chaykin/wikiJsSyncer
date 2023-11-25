package ru.chaykin.wjss.utils.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ru.chaykin.wjss.config.ApplicationConfig;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.link.LinkManager;

public class PageManager {
    private final LinkManager linkManager;

    public PageManager(Context context) {
	this(new LinkManager(context));
    }

    public PageManager(LinkManager linkManager) {
	this.linkManager = linkManager;
    }

    public static Path toLocalPath(String type, String locale, String remotePath) {
	String repoPath = ApplicationConfig.get("wiki.js.pages.repository");
	String extension = PageContentType.of(type).getExtension();

	return Path.of(repoPath, locale, String.format("%s.%s", remotePath, extension));
    }

    public void writePageContent(Path path, String pageContent) throws IOException {
	String wrapedContent = linkManager.wrapByLocalLinks(pageContent);

	Files.createDirectories(path.getParent());
	Files.writeString(path, wrapedContent);
    }

    public String readPageContent(Path path) throws IOException {
	String content = Files.readString(path);
	return linkManager.unwrapLocalLinks(content);
    }
}
