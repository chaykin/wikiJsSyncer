package ru.chaykin.wjss.action;

import java.util.Map;

import ru.chaykin.wjss.action.impl.LocalUpdatedChangeTypeAction;
import ru.chaykin.wjss.action.impl.RemoteNewChangeTypeAction;
import ru.chaykin.wjss.action.impl.RemoteUpdatedChangeTypeAction;
import ru.chaykin.wjss.calc.ChangeType;

public class ChangeTypeActionFactory {
    private static final Map<ChangeType, IChangeTypeAction> ACTIONS = Map.of(
		    ChangeType.REMOTE_NEW, new RemoteNewChangeTypeAction(),
		    ChangeType.REMOTE_UPDATED, new RemoteUpdatedChangeTypeAction(),
		    //ChangeType.REMOTE_DELETED, new RemoteDeletedChangeTypeAction(),
		    ChangeType.LOCAL_UPDATED, new LocalUpdatedChangeTypeAction()/*,
		    ChangeType.LOCAL_DELETED, new LocalDeletedChangeTypeAction()*/);

    public static IChangeTypeAction create(ChangeType changeType) {
	return ACTIONS.get(changeType);
    }
}
