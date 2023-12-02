package ru.chaykin.wjss.change.asset;

import ru.chaykin.wjss.change.ResourceChange;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.ServerAsset;

public class AssetChange extends ResourceChange<LocalAsset, ServerAsset, IAsset> {

    public AssetChange(LocalAsset localAsset, ServerAsset remoteAsset) {
	super(localAsset, remoteAsset);
    }
}