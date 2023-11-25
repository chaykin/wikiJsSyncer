package ru.chaykin.wjss.action.impl.asset;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.chaykin.wjss.change.ChangeType;

import static ru.chaykin.wjss.change.ChangeType.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssetChangeTypeActionFactory {
    private static final Map<ChangeType, IAssetChangeTypeAction> ACTIONS = Map.of(
		    REMOTE_NEW, new RemoteNewChangeTypeAction(),
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

    public static IAssetChangeTypeAction create(ChangeType changeType) {
	return ACTIONS.get(changeType);
    }
}
