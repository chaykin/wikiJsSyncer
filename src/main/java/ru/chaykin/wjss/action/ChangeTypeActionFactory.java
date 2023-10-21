package ru.chaykin.wjss.action;

import java.util.Map;

import ru.chaykin.wjss.action.impl.LocalDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.LocalUpdatedChangeTypeAction;
import ru.chaykin.wjss.action.impl.RemoteDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.RemoteNewChangeTypeAction;
import ru.chaykin.wjss.action.impl.RemoteUpdatedChangeTypeAction;
import ru.chaykin.wjss.calc.ChangeType;

import static ru.chaykin.wjss.calc.ChangeType.*;

public class ChangeTypeActionFactory {
    private static final Map<ChangeType, IChangeTypeAction> ACTIONS = Map.of(
		    REMOTE_NEW, new RemoteNewChangeTypeAction(),
		    REMOTE_UPDATED, new RemoteUpdatedChangeTypeAction(),
		    REMOTE_DELETED, new RemoteDeletedChangeTypeAction(),
		    LOCAL_UPDATED, new LocalUpdatedChangeTypeAction(),
		    LOCAL_DELETED, new LocalDeletedChangeTypeAction());

    public static IChangeTypeAction create(ChangeType changeType) {
	return ACTIONS.get(changeType);
    }
}
