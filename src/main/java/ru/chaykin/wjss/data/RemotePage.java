package ru.chaykin.wjss.data;

import java.nio.file.Path;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.config.ApplicationConfig;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.PageListQuery.PageListItem;
import ru.chaykin.wjss.graphql.query.PageQuery;
import ru.chaykin.wjss.graphql.query.PageQuery.PageItem;
import ru.chaykin.wjss.utils.PageContentType;
import ru.chaykin.wjss.utils.PageHashUtils;

@RequiredArgsConstructor
public class RemotePage implements IPage {
    private final ClientApi api;
    private final PageListItem pageListItem;

    private PageItem pageItem;

    @Override
    public long getId() {
	return pageListItem.id();
    }

    @Override
    public String getTitle() {
	return pageListItem.title();
    }

    @Override
    public String description() {
	return pageListItem.description();
    }

    @Override
    public String getLocale() {
	return pageListItem.locale();
    }

    @Override
    public String getRemotePath() {
	return pageListItem.path();
    }

    @Override
    public Path getLocalPath() {
	String repoPath = ApplicationConfig.get("wiki.js.pages.repository");
	String extension = PageContentType.of(pageListItem.contentType()).getExtension();

	return Path.of(repoPath, String.format("%s.%s", getRemotePath(), extension));
    }

    @Override
    public String getContentType() {
	return pageListItem.contentType();
    }

    @Override
    public long getRemoteUpdatedAt() {
	return pageListItem.updatedAt().getTime();
    }

    @Override
    public String getMd5Hash() {
	return PageHashUtils.md5PageHash(this);
    }

    @Override
    public String getContent() {
	return getPageItem().content();
    }

    @Override
    public List<String> getTags() {
	return pageListItem.tags();
    }

    private PageItem getPageItem() {
	if (pageItem == null) {
	    pageItem = new PageQuery(api).fetchPage(getId());
	    if (pageItem.updatedAt().getTime() != getRemoteUpdatedAt()) {
		throw new RuntimeException(String.format("Page %s is out of date!", getId()));
	    }
	}

	return pageItem;
    }
}
