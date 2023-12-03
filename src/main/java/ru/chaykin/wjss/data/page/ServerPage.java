package ru.chaykin.wjss.data.page;

import java.nio.file.Path;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.IServerResource;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.PageListQuery.PageListItem;
import ru.chaykin.wjss.graphql.query.PageQuery;
import ru.chaykin.wjss.graphql.query.PageQuery.PageItem;
import ru.chaykin.wjss.utils.page.PageHashUtils;
import ru.chaykin.wjss.utils.page.PageManager;

@RequiredArgsConstructor
public class ServerPage implements IPage, IServerResource {
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
    public String getServerPath() {
	return pageListItem.path();
    }

    @Override
    public Path getLocalPath() {
	return PageManager.toLocalPath(pageListItem.contentType(), getLocale(), getServerPath());
    }

    @Override
    public String getContentType() {
	return pageListItem.contentType();
    }

    @Override
    public long getServerUpdatedAt() {
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

    @Override
    public String toString() {
	return String.format("ServerPage[id=%s, contentType=%s, md5Hash=%s, serverPath=%s]",
			getId(), getContentType(), getMd5Hash(), getServerPath());
    }

    private PageItem getPageItem() {
	if (pageItem == null) {
	    pageItem = new PageQuery(api).fetchPage(getId());
	    if (pageItem.updatedAt().getTime() != getServerUpdatedAt()) {
		throw new RuntimeException(String.format("Page %s is out of date!", getId()));
	    }
	}

	return pageItem;
    }
}
