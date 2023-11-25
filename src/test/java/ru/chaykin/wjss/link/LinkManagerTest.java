package ru.chaykin.wjss.link;

import java.nio.file.Path;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.chaykin.wjss.calc.page.LocalPageFetcher;
import ru.chaykin.wjss.calc.page.RemotePageFetcher;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.utils.page.PageManager;

import static ru.chaykin.wjss.calc.page.LocalPageFactory.LOCALE;
import static ru.chaykin.wjss.calc.page.LocalPageFactory.createLocalPage;
import static ru.chaykin.wjss.calc.page.PageFetcherFactory.createLocalPageFetcher;
import static ru.chaykin.wjss.calc.page.PageFetcherFactory.remotePageFetcher;
import static ru.chaykin.wjss.calc.page.RemotePageFactory.createRemotePage;

class LinkManagerTest {
    private static final String REMOTE_PAGE_CONTENT = """
		    Test [link-1](/ru/common/p2) delimeter-1
		    [link-2](/ru/common/p2#section-1) delimeter-2
		    [link-3](/ru/common/p3) delimeter-3
		    [link-4](http://example.com)""";

    private static final String LOCAL_PAGE_CONTENT = """
		    Test [link-1](file://%1$s) delimeter-1
		    [link-2](file://%1$s#section-1) delimeter-2
		    [link-3](/ru/common/p3) delimeter-3
		    [link-4](http://example.com)""";

    @Test
    void wrapTest() {
	RemotePageFetcher remotePageFetcher = remotePageFetcher(
			createRemotePage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(createLocalPageFetcher())
			.remotePageFetcher(remotePageFetcher)
			.build();
	LinkManager linkManager = new LinkManager(context);

	String wrapped = linkManager.wrapByLocalLinks(REMOTE_PAGE_CONTENT);

	Path localPath = PageManager.toLocalPath("markdown", LOCALE, "common/p2");
	String expected = String.format(LOCAL_PAGE_CONTENT, localPath.toAbsolutePath());
	Assertions.assertEquals(expected, wrapped);
    }

    @Test
    void unwrapTest() {
	LocalPageFetcher localPageFetcher = createLocalPageFetcher(
			createLocalPage(2L, "common/p2", new Date(), "PAGE_2"));

	Context context = Context.createBuilder(null, null)
			.localPageFetcher(localPageFetcher)
			.remotePageFetcher(remotePageFetcher())
			.build();
	LinkManager linkManager = new LinkManager(context);

	Path localPath = PageManager.toLocalPath("markdown", LOCALE, "common/p2");
	String localPageContent = String.format(LOCAL_PAGE_CONTENT, localPath.toAbsolutePath());

	String unwrapped = linkManager.unwrapLocalLinks(localPageContent);
	Assertions.assertEquals(REMOTE_PAGE_CONTENT, unwrapped);
    }
}
