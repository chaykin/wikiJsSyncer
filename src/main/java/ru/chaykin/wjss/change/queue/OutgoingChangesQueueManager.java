package ru.chaykin.wjss.change.queue;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.git.GitManager;

@RequiredArgsConstructor
public class OutgoingChangesQueueManager {
    private static final String FETCH_QUEUE_QUERY = "SELECT commit_hash FROM outgoing_queue ORDER BY queue_id";
    private static final String PUT_QUEUE_QUERY = "INSERT INTO outgoing_queue(commit_hash) VALUES(?)";
    private static final String CLEAR_QUEUE_QUERY = "DELETE FROM outgoing_queue";

    private final GitManager gitMan = GitManager.instance();
    private final Context context;

    public Collection<DiffEntry> collectChanges() throws GitAPIException, SQLException, IOException {
	String headHash = gitMan.getHeadCommitHash();

	Collection<String> queuedHashes = fetchQueuedHashes();
	if (!queuedHashes.contains(headHash)) {
	    queueHash(headHash);
	    queuedHashes.add(headHash);
	}

	Collection<DiffEntry> changes = new ArrayList<>();
	for (String hash : queuedHashes) {
	    changes.addAll(gitMan.getAffectedFiles(hash));
	}

	return changes;
    }

    public void clearQueue() throws SQLException {
	DatabaseUtils.update(context.connection(), CLEAR_QUEUE_QUERY);
    }

    private Collection<String> fetchQueuedHashes() throws SQLException {
	try (var statement = context.connection().prepareStatement(FETCH_QUEUE_QUERY)) {
	    Collection<String> hashes = new LinkedHashSet<>();

	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		hashes.add(rs.getString("commit_hash"));
	    }

	    return hashes;
	}
    }

    private void queueHash(String hash) throws SQLException {
	DatabaseUtils.update(context.connection(), PUT_QUEUE_QUERY, hash);
    }
}
