package ru.chaykin.wjss.action.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class RemoteNewChangeTypeAction implements IChangeTypeAction {
    private static final String INSERT_PAGE_QUERY = """
		    INSERT INTO pages(
		    		id, title, description,
		    		locale, remote_path, local_path,
		    		content_type, remote_update_at, md5_hash, tags)
		    	VALUES(?,?,?,?,?,?,?,?,?,?)""";

    @Override
    public void execute(Context context, PageChange pageChange) {
	IPage page  = pageChange.getRemotePage();
	log.debug("Creating new local page: {}", page);

	try {
	    Files.createDirectories(page.getLocalPath().getParent());
	    Files.writeString(page.getLocalPath(), page.getContent());

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
