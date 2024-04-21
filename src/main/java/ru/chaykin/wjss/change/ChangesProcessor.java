package ru.chaykin.wjss.change;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.action.ChangeTypeAction;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IResource;

@RequiredArgsConstructor
public class ChangesProcessor {
    private final Context context;
    private final String indent;

    public boolean processChanges(Map<Long, ChangeType> changes, Function<ChangeType, ChangeTypeAction> actionFactory) {
	boolean hasChanges = false;
	for (Entry<Long, ChangeType> e : changes.entrySet()) {
	    Long id = e.getKey();
	    ChangeType changeType = e.getValue();
	    ChangeTypeAction action = actionFactory.apply(changeType);

	    boolean success = action.execute(context, id);
	    hasChanges |= success;

	    IResource resource = action.actionResource(context, id);
	    reportToConsole(changeType, resource, success);
	}

	return hasChanges;
    }

    private void reportToConsole(ChangeType changeType, IResource resource, boolean success) {
	if (success) {
	    System.out.printf("%s[%s] (%s) %s%n", indent, changeType, resource.getId(), resource.getServerPath());
	}
    }
}
