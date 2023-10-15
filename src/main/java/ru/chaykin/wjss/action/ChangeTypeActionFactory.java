package ru.chaykin.wjss.action;

import java.util.Map;

import ru.chaykin.wjss.action.impl.RemoteNewChangeTypeAction;
import ru.chaykin.wjss.calc.ChangeType;

public class ChangeTypeActionFactory {
    private static final Map<ChangeType, IChangeTypeAction> ACTIONS = Map.of(
		    ChangeType.REMOTE_NEW, new RemoteNewChangeTypeAction()
    );

    public static IChangeTypeAction create(ChangeType changeType) {
	return ACTIONS.get(changeType);
    }
}
