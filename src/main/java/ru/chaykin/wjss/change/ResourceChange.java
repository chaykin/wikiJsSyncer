package ru.chaykin.wjss.change;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.data.IRemoteResource;
import ru.chaykin.wjss.data.IResource;

@Getter
@RequiredArgsConstructor
public class ResourceChange<L extends ILocalResource, R extends IRemoteResource, C extends IResource> {
    private final L localResource;
    private final R remoteResource;

    private ChangeType remoteChange;
    private ChangeType localChange;

    public void setLocalChange(ChangeType changeType) {
	if (localChange != null) {
	    throw new IllegalArgumentException("Local change was set already");
	}

	localChange = changeType;
    }

    public void setRemoteChange(ChangeType changeType) {
	if (remoteChange != null) {
	    throw new IllegalArgumentException("Remote change was set already");
	}

	remoteChange = changeType;
    }

    public boolean hasConflicts() {
	return localChange != null && remoteChange != null;
    }

    public ChangeType getChange() {
	if (hasConflicts()) {
	    throw new IllegalStateException("Could not return change in conflicted state");
	}

	return Optional.ofNullable(localChange).orElse(remoteChange);
    }

    public C getResource() {
	//noinspection unchecked
	C local = (C) localResource;
	//noinspection unchecked
	C remote = (C) remoteResource;

	return Optional.ofNullable(local).orElse(remote);
    }

    @Override
    public String toString() {
	String changes = Stream.of(localChange, remoteChange)
			.filter(Objects::nonNull)
			.map(ChangeType::toString)
			.collect(Collectors.joining(", "));

	return String.format("[%s] (%s) %s", changes, getResource().getId(), getResource().getRemotePath());
    }
}
