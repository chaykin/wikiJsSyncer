package ru.chaykin.wjss.action.impl.page.outgoing;

import java.sql.SQLException;
import java.util.Date;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.ChangeTypeAction;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.graphql.mutation.UpdatePageMutation;
import ru.chaykin.wjss.utils.page.PageHashUtils;

@Log4j2
public class OutgoingUpdatedChangeTypeAction extends ChangeTypeAction {
    private static final String UPDATE_PAGE_QUERY = """
		    UPDATE pages SET
		    	server_update_at = ?,
		    	md5_hash = ?
		    WHERE id = ?""";

    @Override
    public void doExecute(Context context, Long id) {
	IPage page = actionResource(context, id);
	log.debug("Uploading updates to server page: {}", page);

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

    @Override
    public IPage actionResource(Context context, Long id) {
	return context.localPages().get(id);
    }
}
