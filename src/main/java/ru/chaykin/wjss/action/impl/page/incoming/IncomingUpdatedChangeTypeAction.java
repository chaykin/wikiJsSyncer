package ru.chaykin.wjss.action.impl.page.incoming;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.action.ChangeTypeAction;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.LocalPage;
import ru.chaykin.wjss.data.page.ServerPage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class IncomingUpdatedChangeTypeAction extends ChangeTypeAction {
    private static final String UPDATE_PAGE_QUERY = """
		    UPDATE pages SET
		    	server_path = ?,
		    	local_path = ?,
		    	server_update_at = ?,
		    	md5_hash = ?,
		    	tags = ?
		    WHERE id = ?""";

    @Override
    public void doExecute(Context context, Long id) {
	LocalPage localPage = context.localPages().get(id);
	ServerPage serverPage = actionResource(context, id);

	log.debug("Updating exists local page: {}", localPage);

	try {
	    DatabaseUtils.update(context.connection(), UPDATE_PAGE_QUERY,
			    serverPage.getServerPath(),
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

    @Override
    public ServerPage actionResource(Context context, Long id) {
	return context.serverPages().get(id);
    }
}
