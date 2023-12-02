package ru.chaykin.wjss.change;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.data.IServerResource;

import static ru.chaykin.wjss.change.ChangeType.*;

public class IncomingChangesResolver {
    public <L extends ILocalResource, S extends IServerResource> Map<Long, ChangeType> resolveChanges(
		    Map<Long, L> localResources, Map<Long, S> serverResources) {
	Map<Long, ChangeType> changes = new HashMap<>();

	for (Entry<Long, S> se : serverResources.entrySet()) {
	    S sr = se.getValue();
	    L lr = localResources.get(se.getKey());

	    if (lr == null) {
		changes.put(sr.getId(), NEW);
	    } else if (isUpdated(lr, sr)) {
		changes.put(sr.getId(), UPDATED);
	    }
	}

	for (Entry<Long, L> le : localResources.entrySet()) {
	    S sr = serverResources.get(le.getKey());
	    L lr = le.getValue();

	    if (sr == null) {
		changes.put(lr.getId(), DELETED);
	    }
	}

	return changes;
    }

    private boolean isUpdated(ILocalResource lr, IServerResource rr) {
	return rr.getServerUpdatedAt() != lr.getServerUpdatedAt() && !Objects.equals(rr.getMd5Hash(), lr.getMd5Hash());
    }
}