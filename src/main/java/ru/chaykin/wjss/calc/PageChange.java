package ru.chaykin.wjss.calc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.data.IPage;
import ru.chaykin.wjss.data.LocalPage;
import ru.chaykin.wjss.data.RemotePage;

@RequiredArgsConstructor
public class PageChange {

    @Getter
    private final LocalPage localPage;

    @Getter
    private final RemotePage remotePage;

    private final Set<ChangeType> changes = new HashSet<>();

    public void addChange(ChangeType changeType) {
	changes.add(changeType);
    }

    public Set<ChangeType> getChanges() {
	return Collections.unmodifiableSet(changes);
    }

    public boolean hasConflicts() {
	return changes.size() > 1;
    }

    public IPage getPage() {
	return Optional.<IPage>ofNullable(localPage).orElse(remotePage);
    }

    @Override
    public String toString() {
	return String.format("[%s] (%s) %s", StringUtils.join(changes, ", "),
			getPage().getId(), getPage().getRemotePath());
    }
}
