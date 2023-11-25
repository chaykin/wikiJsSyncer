package ru.chaykin.wjss.action.impl.page;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.chaykin.wjss.action.impl.page.IPageChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.LocalDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.LocalUpdatedChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.RemoteDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.RemoteNewChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.RemoteUpdatedChangeTypeAction;
import ru.chaykin.wjss.change.ChangeType;

import static ru.chaykin.wjss.change.ChangeType.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageChangeTypeActionFactory {
    private static final Map<ChangeType, IPageChangeTypeAction> ACTIONS = Map.of(
		    REMOTE_NEW, new RemoteNewChangeTypeAction(),
		    REMOTE_UPDATED, new RemoteUpdatedChangeTypeAction(),
		    REMOTE_DELETED, new RemoteDeletedChangeTypeAction(),
		    LOCAL_UPDATED, new LocalUpdatedChangeTypeAction(),
		    LOCAL_DELETED, new LocalDeletedChangeTypeAction());

    public static IPageChangeTypeAction create(ChangeType changeType) {
	return ACTIONS.get(changeType);
    }
}
