package ru.chaykin.wjss.utils.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.link.LinkManager;

import static ru.chaykin.wjss.utils.PathUtils.REPO_PATH;

public class PageManager {
    private static final String PAGES_SUB_PATH = "pages";

    private final LinkManager linkManager;

    public PageManager(Context context) {
	this(new LinkManager(context));
    }

    public PageManager(LinkManager linkManager) {
	this.linkManager = linkManager;
    }

    public static Path toLocalPath(String type, String locale, String remotePath) {
	String extension = PageContentType.of(type).getExtension();
	return Path.of(REPO_PATH, PAGES_SUB_PATH, locale, String.format("%s.%s", remotePath, extension));
    }

    public void writePageContent(Path path, String pageContent) throws IOException {
	String wrappedContent = linkManager.wrapByLocalLinks(pageContent);

	Files.createDirectories(path.getParent());
	Files.writeString(path, wrappedContent);
    }

    public String readPageContent(Path path) throws IOException {
	String content = Files.readString(path);
	return linkManager.unwrapLocalLinks(content);
    }
}
