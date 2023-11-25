package ru.chaykin.wjss.action.impl.page;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.RemotePage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class RemoteUpdatedChangeTypeAction implements IPageChangeTypeAction {

    private static final String UPDATE_PAGE_QUERY = """
		    UPDATE pages SET
		    	remote_path = ?,
		    	local_path = ?, 
		    	remote_update_at = ?,
		    	md5_hash = ?,
		    	tags = ?
		    WHERE id = ?""";

    @Override
    public void execute(Context context, PageChange pageChange) {
	log.debug("Updating exists local page: {}", pageChange.getResource());

	LocalPage localPage = pageChange.getLocalResource();
	RemotePage remotePage = pageChange.getRemoteResource();
	try {
	    DatabaseUtils.update(context.connection(), UPDATE_PAGE_QUERY,
			    remotePage.getRemotePath(),
			    remotePage.getLocalPath(),
			    remotePage.getRemoteUpdatedAt(),
			    remotePage.getMd5Hash(),
			    StringUtils.join(remotePage.getTags(), ","),
			    remotePage.getId());

	    if (!remotePage.getContent().equals(localPage.getContent())) {
		log.debug("Updating page content...");
		context.pageManager().writePageContent(localPage.getLocalPath(), remotePage.getContent());
	    }
	    if (!remotePage.getLocalPath().equals(localPage.getLocalPath())) {
		log.debug("Updating page path...");
		Files.createDirectories(remotePage.getLocalPath().getParent());
		Files.move(localPage.getLocalPath(), remotePage.getLocalPath());
	    }
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}