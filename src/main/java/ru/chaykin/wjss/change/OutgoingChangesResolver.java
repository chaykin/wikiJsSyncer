package ru.chaykin.wjss.change;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.DiffEntry;
import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.data.IResource;
import ru.chaykin.wjss.data.IServerResource;
import ru.chaykin.wjss.git.GitManager;

import static ru.chaykin.wjss.change.ChangeType.DELETED;
import static ru.chaykin.wjss.change.ChangeType.UPDATED;

public class OutgoingChangesResolver {
    private static final Path REPO_PATH = Path.of(GitManager.REPO_PATH);

    private Map<String, Long> localResourceByPath;
    private Map<String, Long> serverResourceByPath;

    public <L extends ILocalResource, S extends IServerResource> Map<Long, ChangeType> resolveChanges(
		    Map<Long, L> localResources, Map<Long, S> serverResources, Collection<DiffEntry> affectedFiles) {
	clear();

	Map<Long, ChangeType> changes = new HashMap<>();
	for (DiffEntry de : affectedFiles) {
	    switch (de.getChangeType()) {
	    case DiffEntry.ChangeType.MODIFY -> {
		String path = de.getNewPath();
		Optional.ofNullable(getServerResource(serverResources, path)).ifPresent(id -> changes.put(id, UPDATED));
	    }
	    case DiffEntry.ChangeType.DELETE -> {
		String path = de.getOldPath();
		Optional.ofNullable(getLocalResource(localResources, path)).ifPresent(id -> changes.put(id, DELETED));
	    }
	    default -> throw new UnsupportedOperationException(
			    "Unsupported change type: %s".formatted(de.getChangeType()));
	    }
	}

	return changes;
    }

    private <L extends ILocalResource> Long getLocalResource(Map<Long, L> localResource, String path) {
	if (localResourceByPath == null) {
	    localResourceByPath = localResource.values().stream()
			    .collect(Collectors.toMap(r -> r.getLocalPath().toString(), IResource::getId));
	}

	return localResourceByPath.get(path);
    }

    private <S extends IServerResource> Long getServerResource(Map<Long, S> serverResources, String path) {
	if (serverResourceByPath == null) {
	    serverResourceByPath = serverResources.values().stream()
			    .collect(Collectors.toMap(this::getRelativeRepoPath, IResource::getId));
	}

	return serverResourceByPath.get(path);
    }

    private void clear() {
	localResourceByPath = null;
	serverResourceByPath = null;
    }

    private String getRelativeRepoPath(IServerResource asset) {
	return REPO_PATH.relativize(asset.getLocalPath()).toString();
    }
}
