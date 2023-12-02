package ru.chaykin.wjss.action.impl.asset;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.incoming.IncomingDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.incoming.IncomingNewChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.incoming.IncomingUpdatedChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.outgoing.OutgoingDeletedChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.outgoing.OutgoingUpdatedChangeTypeAction;
import ru.chaykin.wjss.change.ChangeType;

import static ru.chaykin.wjss.change.ChangeType.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssetChangeTypeActionFactory {
    private static final Map<ChangeType, IAssetChangeTypeAction> ACTIONS = Map.of(
		    REMOTE_NEW, new IncomingNewChangeTypeAction(),
		    REMOTE_UPDATED, (c, a) -> {
			//TODO
		    },
		    REMOTE_DELETED, (c, a) -> {
			//TODO
		    },
		    LOCAL_UPDATED, (c, a) -> {
			//TODO
		    },
		    LOCAL_DELETED, (c, a) -> {
			//TODO
		    });

    private static final Map<ChangeType, IChangeTypeAction> INCOMING_ACTIONS = Map.of(
		    NEW, new IncomingNewChangeTypeAction(),
		    UPDATED, new IncomingUpdatedChangeTypeAction(),
		    DELETED, new IncomingDeletedChangeTypeAction()
    );

    private static final Map<ChangeType, IChangeTypeAction> OUTGOING_ACTIONS = Map.of(
		    UPDATED, new OutgoingUpdatedChangeTypeAction(),
		    DELETED, new OutgoingDeletedChangeTypeAction()
    );

    public static IAssetChangeTypeAction create(ChangeType changeType) {
	return ACTIONS.get(changeType);
    }

    public static IChangeTypeAction createIncoming(ChangeType changeType) {
	return INCOMING_ACTIONS.get(changeType);
    }

    public static IChangeTypeAction createOutgoing(ChangeType changeType) {
	return OUTGOING_ACTIONS.get(changeType);
    }
}
