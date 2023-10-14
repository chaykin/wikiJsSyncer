package ru.chaykin.wjss.calc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.data.LocalPage;
import ru.chaykin.wjss.data.RemotePage;
import ru.chaykin.wjss.utils.PageHashUtils;

import static ru.chaykin.wjss.calc.ChangeType.*;

@RequiredArgsConstructor
public class ChangesCalc {
    private final LocalPageFetcher localPageFetcher;
    private final RemotePageFetcher remotePageFetcher;

    public ChangesCalc(Context context) {
	this(new LocalPageFetcher(context.connection()), new RemotePageFetcher(context.api()));
    }

    public Collection<PageChange> calculateChanges() {
	Map<Long, RemotePage> remotePages = remotePageFetcher.fetch();
	Map<Long, LocalPage> localPages = localPageFetcher.fetch();

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
