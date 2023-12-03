package ru.chaykin.wjss.action.impl.page.incoming;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class IncomingDeletedChangeTypeAction implements IChangeTypeAction {
    private static final String DELETE_PAGE_QUERY = "DELETE FROM pages WHERE id = ?";

    @Override
    public void execute(Context context, Long id) {
	IPage page = context.localPages().get(id);
	log.debug("Delete exists local page: {}", page);

	try {
	    DatabaseUtils.update(context.connection(), DELETE_PAGE_QUERY, page.getId());
	    Files.delete(page.getLocalPath());
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
