package ru.chaykin.wjss.action.impl.page;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.incoming.IncomingDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.incoming.IncomingNewChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.incoming.IncomingUpdatedChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.outgoing.OutgoingDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.page.outgoing.OutgoingUpdatedChangeTypeAction;
import ru.chaykin.wjss.change.ChangeType;

import static ru.chaykin.wjss.change.ChangeType.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageChangeTypeActionFactory {
    private static final Map<ChangeType, IChangeTypeAction> INCOMING_ACTIONS = Map.of(
		    NEW, new IncomingNewChangeTypeAction(),
		    UPDATED, new IncomingUpdatedChangeTypeAction(),
		    DELETED, new IncomingDeletedChangeTypeAction()
    );

    private static final Map<ChangeType, IChangeTypeAction> OUTGOING_ACTIONS = Map.of(
		    UPDATED, new OutgoingUpdatedChangeTypeAction(),
		    DELETED, new OutgoingDeletedChangeTypeAction()
    );

    public static IChangeTypeAction createIncoming(ChangeType changeType) {
	return INCOMING_ACTIONS.get(changeType);
    }

    public static IChangeTypeAction createOutgoing(ChangeType changeType) {
	return OUTGOING_ACTIONS.get(changeType);
    }
}