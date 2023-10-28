package ru.chaykin.wjss.conflict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.utils.PathUtils;

@Log4j2
@RequiredArgsConstructor
public class PostponedConflictsProcessor {
    private static final String UPDATE_PAGE_QUERY = """
		    UPDATE pages SET
		    	remote_path = ?,
		    	remote_update_at = ?,
		    	md5_hash = ?,
		    	tags = ?
		    WHERE id = ?""";

    private static final String DELETE_CONFLICT_QUERY = "DELETE FROM conflicts WHERE id = ?";

    private final Context context;

    public boolean processConflicts() {
	try (var statement = context.connection().prepareStatement("SELECT * FROM conflicts")) {
	    boolean success = true;

	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		long id = rs.getLong("id");
		log.debug("Checking conflict for page {}", id);

		Path minePath = Path.of(rs.getString("local_path_mine"));
		Path theirsPath = Path.of(rs.getString("local_path_theirs"));
		Path resolvedPath = Path.of(PathUtils.appendToFileName(minePath, "resolved"));

		if (Files.exists(resolvedPath)) {
		    log.debug("Conflict resolved");

		    Files.deleteIfExists(minePath);
		    Files.deleteIfExists(theirsPath);
		    Files.move(resolvedPath, minePath);

		    DatabaseUtils.update(context.connection(), UPDATE_PAGE_QUERY,
				    rs.getString("remote_path"),
				    rs.getLong("remote_update_at"),
				    rs.getString("remote_md5_hash"),
				    rs.getString("remote_tags"),
				    id);
		    DatabaseUtils.update(context.connection(), DELETE_CONFLICT_QUERY, id);
		    context.connection().commit();
		} else {
		    System.out.printf("Conflict in file %s not resolved%n", minePath);
		    success = false;
		}
	    }

	    return success;
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
