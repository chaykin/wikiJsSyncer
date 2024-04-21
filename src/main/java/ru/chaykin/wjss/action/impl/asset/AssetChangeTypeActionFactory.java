package ru.chaykin.wjss.action.impl.asset;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.chaykin.wjss.action.ChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.incoming.IncomingDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.incoming.IncomingNewChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.incoming.IncomingUpdatedChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.outgoing.OutgoingDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.outgoing.OutgoingUpdatedChangeTypeAction;
import ru.chaykin.wjss.change.ChangeType;

import static ru.chaykin.wjss.change.ChangeType.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssetChangeTypeActionFactory {
    private static final Map<ChangeType, ChangeTypeAction> INCOMING_ACTIONS = Map.of(
		    NEW, new IncomingNewChangeTypeAction(),
		    UPDATED, new IncomingUpdatedChangeTypeAction(),
		    DELETED, new IncomingDeletedChangeTypeAction()
    );

    private static final Map<ChangeType, ChangeTypeAction> OUTGOING_ACTIONS = Map.of(
		    UPDATED, new OutgoingUpdatedChangeTypeAction(),
		    DELETED, new OutgoingDeletedChangeTypeAction()
    );

    public static ChangeTypeAction createIncoming(ChangeType changeType) {
	return INCOMING_ACTIONS.get(changeType);
    }

    public static ChangeTypeAction createOutgoing(ChangeType changeType) {
	return OUTGOING_ACTIONS.get(changeType);
    }
}
