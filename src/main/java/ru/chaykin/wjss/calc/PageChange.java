package ru.chaykin.wjss.calc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.data.IPage;

@RequiredArgsConstructor
public class PageChange {

    @Getter
    private final IPage page;
    private final Set<ChangeType> changes = new HashSet<>();

    public void addChange(ChangeType changeType) {
	changes.add(changeType);
    }

    public Set<ChangeType> getChanges() {
	return Collections.unmodifiableSet(changes);
    }

    @Override
    public String toString() {
	return String.format("[%s] (%s) %s", StringUtils.join(changes, ", "), page.getId(), page.getRemotePath());
    }
}
