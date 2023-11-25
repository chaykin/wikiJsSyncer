package ru.chaykin.wjss.action.impl.asset;

import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.change.asset.AssetChange;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.RemoteAsset;

public interface IAssetChangeTypeAction extends IChangeTypeAction<LocalAsset, RemoteAsset, IAsset, AssetChange> {
}
