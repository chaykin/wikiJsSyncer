package ru.chaykin.wjss.change.asset;

import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.AssetChangeTypeActionFactory;
import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.calc.asset.AssetChangesCalc;
import ru.chaykin.wjss.change.ChangeType;
import ru.chaykin.wjss.change.ChangesProcessor;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.RemoteAsset;

public class AssetChangesProcessor extends ChangesProcessor<LocalAsset, RemoteAsset, IAsset, AssetChange> {

    @Override
    protected ChangesCalc<LocalAsset, RemoteAsset, IAsset, AssetChange> createChangeCalc(Context context) {
	return new AssetChangesCalc(context);
    }

    @Override
    protected void resolveConflicts(Context context, AssetChange change) {
	//TODO
    }

    @Override
    protected IChangeTypeAction<LocalAsset, RemoteAsset, IAsset, AssetChange> createChangeTypeAction(
		    ChangeType changeType) {
	return AssetChangeTypeActionFactory.create(changeType);
    }
}