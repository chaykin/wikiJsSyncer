package ru.chaykin.wjss.calc.asset;

import java.util.Map;

import ru.chaykin.wjss.calc.ChangesCalc;
import ru.chaykin.wjss.change.asset.AssetChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.RemoteAsset;
import ru.chaykin.wjss.utils.asset.AssetHashUtils;

public class AssetChangesCalc extends ChangesCalc<LocalAsset, RemoteAsset, IAsset, AssetChange> {

    public AssetChangesCalc(Context context) {
	super(context);
    }

    @Override
    protected Map<Long, RemoteAsset> getRemoteResources() {
	return context.remoteAssets();
    }

    @Override
    protected Map<Long, LocalAsset> getLocalResources() {
	return context.localAssets();
    }

    @Override
    protected String md5ResourceHash(LocalAsset localAsset) {
	return AssetHashUtils.md5PageHash(localAsset);
    }

    @Override
    protected AssetChange newResourceChange(LocalAsset localAsset, RemoteAsset remoteAsset) {
	return new AssetChange(localAsset, remoteAsset);
    }
}