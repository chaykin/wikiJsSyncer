package ru.chaykin.wjss.action.impl;

import java.sql.SQLException;
import java.util.Date;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.graphql.mutation.UpdatePageMutation;
import ru.chaykin.wjss.utils.PageHashUtils;

@Log4j2
public class LocalUpdatedChangeTypeAction implements IChangeTypeAction {
    private static final String UPDATE_PAGE_QUERY = """
		    UPDATE pages SET 
		    	remote_update_at = ?,
		    	md5_hash = ?
		    WHERE id = ?""";

    @Override
    public void execute(Context context, PageChange pageChange) {
	IPage page  = pageChange.getLocalPage();
	log.debug("Uploading updates to remote page: {}", page);

	Date updatedAt = new UpdatePageMutation(context.api()).updatePage(page);

	try {
	    DatabaseUtils.update(context.connection(), UPDATE_PAGE_QUERY,
			    updatedAt.getTime(),
			    PageHashUtils.md5PageHash(page),
			    page.getId());
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
