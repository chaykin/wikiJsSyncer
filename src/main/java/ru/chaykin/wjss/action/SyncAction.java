package ru.chaykin.wjss.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.action.change.ChangeType;
import ru.chaykin.wjss.action.change.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.data.LocalPage;
import ru.chaykin.wjss.data.RemotePage;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.query.PageListQuery;
import ru.chaykin.wjss.utils.PageHashUtils;

import static java.util.function.Function.identity;
import static ru.chaykin.wjss.action.change.ChangeType.*;

@RequiredArgsConstructor
public class SyncAction {
    private final Context context;

    public void execute() {
	Map<Long, RemotePage> remotePages = fetchRemotePages(context.api());
	Map<Long, LocalPage> localPages = fetchLocalPages(context.connection());

	Collection<PageChange> changes = calcPageChanges(remotePages, localPages);
	changes.forEach(System.out::println);
    }

    private Map<Long, RemotePage> fetchRemotePages(ClientApi api) {
	PageListQuery query = new PageListQuery(api);

	return query.fetchPages().stream()
			.map(p -> new RemotePage(api, p))
			.collect(Collectors.toMap(RemotePage::getId, identity()));
    }

    private Map<Long, LocalPage> fetchLocalPages(Connection connection) {
	try (var statement = connection.prepareStatement("SELECT * FROM pages")) {
	    Map<Long, LocalPage> pages = new HashMap<>();

	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		LocalPage page = new LocalPage(rs);
		pages.put(page.getId(), page);
	    }

	    return pages;
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    private Collection<PageChange> calcPageChanges(Map<Long, RemotePage> remotePages, Map<Long, LocalPage> localPages) {
	Map<Long, PageChange> changes = new HashMap<>();

	for (Entry<Long, RemotePage> pe : remotePages.entrySet()) {
	    RemotePage rp = pe.getValue();
	    LocalPage lp = localPages.get(pe.getKey());

	    if (lp == null) {
		addChange(changes, rp, REMOTE_NEW);
	    } else if (isRemoteUpdated(rp, lp)) {
		addChange(changes, rp, REMOTE_UPDATED);
	    }
	}

	for (Entry<Long, LocalPage> le : localPages.entrySet()) {
	    RemotePage rp = remotePages.get(le.getKey());
	    LocalPage lp = le.getValue();

	    if (rp == null) {
		addChange(changes, lp, REMOTE_DELETED);
	    } else if (lp.getContent() == null) {
		addChange(changes, lp, LOCAL_DELETED);
	    } else if (isLocalUpdated(lp)) {
		addChange(changes, lp, LOCAL_UPDATED);
	    }
	}

	return changes.values();
    }

    private boolean isRemoteUpdated(RemotePage rp, LocalPage lp) {
	return rp.getRemoteUpdatedAt() != lp.getRemoteUpdatedAt() && !Objects.equals(rp.getMd5Hash(), lp.getMd5Hash());
    }

    private boolean isLocalUpdated(LocalPage lp) {
	return !Objects.equals(lp.getMd5Hash(), PageHashUtils.md5PageHash(lp));
    }

    private void addChange(Map<Long, PageChange> changes, IPage page, ChangeType changeType) {
	changes.computeIfAbsent(page.getId(), k -> new PageChange(page)).addChange(changeType);
    }
}
