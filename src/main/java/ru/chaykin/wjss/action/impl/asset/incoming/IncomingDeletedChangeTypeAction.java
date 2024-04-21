package ru.chaykin.wjss.action.impl.asset.incoming;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.ChangeTypeAction;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class IncomingDeletedChangeTypeAction extends ChangeTypeAction {
    private static final String DELETE_ASSET_QUERY = "DELETE FROM assets WHERE id = ?";

    @Override
    public void doExecute(Context context, Long id) {
	IAsset asset = actionResource(context, id);
	log.debug("Delete exists local asset: {}", asset);

	try {
	    DatabaseUtils.update(context.connection(), DELETE_ASSET_QUERY, id);
	    Files.delete(asset.getLocalPath());
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public IAsset actionResource(Context context, Long id) {
	return context.localAssets().get(id);
    }
}
