package ru.chaykin.wjss.change;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.context.Context;

@RequiredArgsConstructor
public class ChangesProcessor {
    private final Context context;

    public void processChanges(Map<Long, ChangeType> changes,
		    Function<ChangeType, IChangeTypeAction> actionFactory) {
	for (Entry<Long, ChangeType> e : changes.entrySet()) {
	    IChangeTypeAction action = actionFactory.apply(e.getValue());
	    action.execute(context, e.getKey());
	}
    }
}
