package ru.chaykin.wjss.action.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class RemoteDeletedChangeTypeAction implements IChangeTypeAction {
    private static final String DELETE_PAGE_QUERY = "DELETE FROM pages WHERE id = ?";

    @Override
    public void execute(Context context, PageChange pageChange) {
	log.debug("Delete exists local page: {}", pageChange.getPage());

	IPage page = pageChange.getPage();
	try {
	    DatabaseUtils.update(context.connection(), DELETE_PAGE_QUERY, page.getId());
	    Files.delete(page.getLocalPath());
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}