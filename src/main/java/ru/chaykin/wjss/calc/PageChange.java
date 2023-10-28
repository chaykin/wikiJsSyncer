package ru.chaykin.wjss.calc;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.data.LocalPage;
import ru.chaykin.wjss.data.RemotePage;

@Getter
@RequiredArgsConstructor
public class PageChange {
    private final LocalPage localPage;
    private final RemotePage remotePage;

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

    public IPage getPage() {
	return Optional.<IPage>ofNullable(localPage).orElse(remotePage);
    }

    @Override
    public String toString() {
	String changes = Stream.of(localChange, remoteChange)
			.filter(Objects::nonNull)
			.map(ChangeType::toString)
			.collect(Collectors.joining(", "));

	return String.format("[%s] (%s) %s", changes, getPage().getId(), getPage().getRemotePath());
    }
}
