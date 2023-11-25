package ru.chaykin.wjss.calc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.change.ResourceChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.data.IRemoteResource;
import ru.chaykin.wjss.data.IResource;

import static ru.chaykin.wjss.change.ChangeType.*;

@RequiredArgsConstructor
public abstract class ChangesCalc<L extends ILocalResource, R extends IRemoteResource,
		C extends IResource, RC extends ResourceChange<L, R, C>> {
    protected final Context context;

    public Collection<RC> calculateChanges() {
	Map<Long, R> remoteResources = getRemoteResources();
	Map<Long, L> localResources = getLocalResources();

	Map<Long, RC> changes = new HashMap<>();

	for (Entry<Long, R> re : remoteResources.entrySet()) {
	    R rr = re.getValue();
	    L lr = localResources.get(re.getKey());

	    if (lr == null) {
		computeResourceChange(changes, lr, rr).setRemoteChange(REMOTE_NEW);
	    } else if (isRemoteUpdated(lr, rr)) {
		computeResourceChange(changes, lr, rr).setRemoteChange(REMOTE_UPDATED);
	    }
	}

	for (Entry<Long, L> le : localResources.entrySet()) {
	    R rr = remoteResources.get(le.getKey());
	    L lr = le.getValue();

	    if (rr == null) {
		computeResourceChange(changes, lr, rr).setRemoteChange(REMOTE_DELETED);
	    }
	    if (!lr.exists()) {
		computeResourceChange(changes, lr, rr).setLocalChange(LOCAL_DELETED);
	    } else if (isLocalUpdated(lr)) {
		computeResourceChange(changes, lr, rr).setLocalChange(LOCAL_UPDATED);
	    }
	}

	return changes.values();
    }

    protected abstract Map<Long, R> getRemoteResources();

    protected abstract Map<Long, L> getLocalResources();

    protected abstract String md5ResourceHash(L localResource);

    protected abstract RC newResourceChange(L localResource, R remoteResource);

    private boolean isRemoteUpdated(L lr, R rr) {
	return rr.getRemoteUpdatedAt() != lr.getRemoteUpdatedAt() && !Objects.equals(rr.getMd5Hash(), lr.getMd5Hash());
    }

    private boolean isLocalUpdated(L lr) {
	return !Objects.equals(lr.getMd5Hash(), md5ResourceHash(lr));
    }

    private RC computeResourceChange(Map<Long, RC> changes, L localResource, R remoteResource) {
	long id = Optional.<IResource>ofNullable(localResource).orElse(remoteResource).getId();
	return changes.computeIfAbsent(id, k -> newResourceChange(localResource, remoteResource));
    }
}
