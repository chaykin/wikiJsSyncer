package ru.chaykin.wjss.action.impl.page;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class RemoteDeletedChangeTypeAction implements IPageChangeTypeAction {
    private static final String DELETE_PAGE_QUERY = "DELETE FROM pages WHERE id = ?";

    @Override
    public void execute(Context context, PageChange pageChange) {
	log.debug("Delete exists local page: {}", pageChange.getResource());

	IPage page = pageChange.getResource();
	try {
	    DatabaseUtils.update(context.connection(), DELETE_PAGE_QUERY, page.getId());
	    Files.delete(page.getLocalPath());
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}