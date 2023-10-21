package ru.chaykin.wjss.action.impl;

import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.graphql.mutation.DeletePageMutation;

@Log4j2
public class LocalDeletedChangeTypeAction implements IChangeTypeAction {
    private static final String DELETE_PAGE_QUERY = "DELETE FROM pages WHERE id = ?";

    @Override
    public void execute(Context context, PageChange pageChange) {
	IPage page = pageChange.getPage();
	log.debug("Delete remote page: {}", page);

	try {
	    DatabaseUtils.update(context.connection(), DELETE_PAGE_QUERY, page.getId());
	    new DeletePageMutation(context.api()).deletePage(page);
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}