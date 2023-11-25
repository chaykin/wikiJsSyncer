package ru.chaykin.wjss.action.impl.page;

import java.io.IOException;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class RemoteNewChangeTypeAction implements IPageChangeTypeAction {
    private static final String INSERT_PAGE_QUERY = """
		    INSERT INTO pages(
		    		id, title, description,
		    		locale, remote_path, local_path,
		    		content_type, remote_update_at, md5_hash, tags)
		    	VALUES(?,?,?,?,?,?,?,?,?,?)""";

    @Override
    public void execute(Context context, PageChange pageChange) {
	IPage page = pageChange.getRemoteResource();
	log.debug("Creating new local page: {}", page);

	try {
	    context.pageManager().writePageContent(page.getLocalPath(), page.getContent());

	    DatabaseUtils.update(context.connection(), INSERT_PAGE_QUERY,
			    page.getId(),
			    page.getTitle(),
			    page.description(),
			    page.getLocale(),
			    page.getRemotePath(),
			    page.getLocalPath().toString(),
			    page.getContentType(),
			    page.getRemoteUpdatedAt(),
			    page.getMd5Hash(),
			    StringUtils.join(page.getTags(), ","));
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
