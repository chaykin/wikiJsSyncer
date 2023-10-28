package ru.chaykin.wjss.conflict.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.conflict.ConflictResolver;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.utils.PathUtils;

@Log4j2
public class PostponeConflictResolver extends ConflictResolver {
    private static final String INSERT_CONFLICTS_QUERY = """
		    INSERT INTO conflicts(
		    	id, local_path_mine, local_path_theirs, 
		    	remote_path, remote_update_at, remote_md5_hash, remote_tags) 
		    VALUES(?,?,?,?,?,?,?)""";

    public PostponeConflictResolver(Context context) {
	super(context);
    }

    @Override
    public void resolve(PageChange pageChange) {
	IPage page = pageChange.getRemotePage();
	log.debug("Postpone conflict resolving for page: {}", page);

	String theirsPath = PathUtils.appendToFileName(page.getLocalPath(), "remote");

	try {
	    Files.writeString(Path.of(theirsPath), page.getContent());

	    DatabaseUtils.update(context.connection(), INSERT_CONFLICTS_QUERY,
			    page.getId(),
			    page.getLocalPath(),
			    theirsPath,
			    page.getRemotePath(),
			    page.getRemoteUpdatedAt(),
			    page.getMd5Hash(),
			    String.join(",", page.getTags()));
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
