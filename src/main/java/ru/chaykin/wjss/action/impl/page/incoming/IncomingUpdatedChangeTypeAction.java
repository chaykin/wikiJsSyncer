package ru.chaykin.wjss.action.impl.page.incoming;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.IPageChangeTypeAction;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.ServerPage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class IncomingUpdatedChangeTypeAction
		implements IPageChangeTypeAction, IChangeTypeAction<LocalPage, ServerPage, IPage, PageChange> {
    private static final String UPDATE_PAGE_QUERY = """
		    UPDATE pages SET
		    	remote_path = ?,
		    	local_path = ?, 
		    	remote_update_at = ?,
		    	md5_hash = ?,
		    	tags = ?
		    WHERE id = ?""";

    @Override
    public void execute(Context context, Long id) {
	LocalPage localPage = context.localPages().get(id);
	ServerPage serverPage = context.serverPages().get(id);

	log.debug("Updating exists local page: {}", localPage);

	try {
	    DatabaseUtils.update(context.connection(), UPDATE_PAGE_QUERY,
			    serverPage.getRemotePath(),
			    serverPage.getLocalPath(),
			    serverPage.getServerUpdatedAt(),
			    serverPage.getMd5Hash(),
			    StringUtils.join(serverPage.getTags(), ","),
			    serverPage.getId());

	    if (!serverPage.getContent().equals(localPage.getContent())) {
		log.debug("Updating page content...");
		context.pageManager().writePageContent(localPage.getLocalPath(), serverPage.getContent());
	    }
	    if (!serverPage.getLocalPath().equals(localPage.getLocalPath())) {
		log.debug("Updating page path...");
		Files.createDirectories(serverPage.getLocalPath().getParent());
		Files.move(localPage.getLocalPath(), serverPage.getLocalPath());
	    }
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
