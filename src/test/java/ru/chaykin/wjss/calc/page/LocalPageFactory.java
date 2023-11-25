package ru.chaykin.wjss.calc.page;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.utils.page.PageHashUtils;
import ru.chaykin.wjss.utils.page.PageManager;

import static org.mockito.Mockito.*;
import static ru.chaykin.wjss.utils.page.PageContentType.MARKDOWN;

public class LocalPageFactory {
    public static final String LOCALE = "ru";

    public static LocalPage createLocalPage(long id, String path, Date updatedAt, String content) {
	return createLocalPage(id, path, updatedAt, content, content);
    }

    public static LocalPage createLocalPage(long id, String path, Date updatedAt, String content, String oldContent) {
	LocalPage page = mock();
	when(page.getId()).thenReturn(id);
	when(page.getTitle()).thenReturn("testTitle");
	when(page.description()).thenReturn("testDescr");
	when(page.getLocale()).thenReturn(LOCALE);
	when(page.getRemotePath()).thenReturn(path);
	when(page.getLocalPath()).thenReturn(PageManager.toLocalPath(MARKDOWN.getTypeName(), LOCALE, path));
	when(page.getContentType()).thenReturn(MARKDOWN.getTypeName());
	when(page.getRemoteUpdatedAt()).thenReturn(updatedAt.getTime());
	when(page.getTags()).thenReturn(Collections.emptyList());

	doReturn(content).when(page).getContent();
	doReturn(content != null).when(page).exists();

	String hash = PageHashUtils.md5PageHash(page);
	if (!Objects.equals(content, oldContent)) {
	    hash = createLocalPage(id, path, updatedAt, oldContent).getMd5Hash();
	}

	when(page.getMd5Hash()).thenReturn(hash);

	return page;
    }
}
