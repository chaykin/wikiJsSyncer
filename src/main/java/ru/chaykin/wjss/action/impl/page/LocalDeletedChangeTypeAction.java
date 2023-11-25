package ru.chaykin.wjss.action.impl.page;

import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.change.page.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.graphql.mutation.DeletePageMutation;

@Log4j2
public class LocalDeletedChangeTypeAction implements IPageChangeTypeAction {
    private static final String DELETE_PAGE_QUERY = "DELETE FROM pages WHERE id = ?";

    @Override
    public void execute(Context context, PageChange pageChange) {
	IPage page = pageChange.getResource();
	log.debug("Delete remote page: {}", page);

	try {
	    DatabaseUtils.update(context.connection(), DELETE_PAGE_QUERY, page.getId());
	    new DeletePageMutation(context.api()).deletePage(page);
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}