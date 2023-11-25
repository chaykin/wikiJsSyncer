package ru.chaykin.wjss.change.asset;

import ru.chaykin.wjss.change.ResourceChange;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.RemoteAsset;

public class AssetChange extends ResourceChange<LocalAsset, RemoteAsset, IAsset> {

    public AssetChange(LocalAsset localAsset, RemoteAsset remoteAsset) {
	super(localAsset, remoteAsset);
    }
}