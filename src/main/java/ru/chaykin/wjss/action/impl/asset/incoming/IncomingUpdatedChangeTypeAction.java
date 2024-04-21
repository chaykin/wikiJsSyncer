package ru.chaykin.wjss.action.impl.asset.incoming;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import ru.chaykin.wjss.action.ChangeTypeAction;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.ServerAsset;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.utils.asset.AssetHashUtils;

@Log4j2
public class IncomingUpdatedChangeTypeAction extends ChangeTypeAction {
    private static final String UPDATE_ASSET_QUERY = """
		    UPDATE assets SET
		    	folderId = ?,
		    	server_path = ?,
		    	local_path = ?,
		    	server_update_at = ?,
		    	md5_hash = ?
		    WHERE id = ?""";

    @Override
    public void doExecute(Context context, Long id) {
	LocalAsset localAsset = context.localAssets().get(id);
	ServerAsset serverAsset = actionResource(context, id);

	log.debug("Updating exists local asset: {}", localAsset);

	try {
	    DatabaseUtils.update(context.connection(), UPDATE_ASSET_QUERY,
			    serverAsset.getFolderId(),
			    serverAsset.getServerPath(),
			    serverAsset.getLocalPath(),
			    serverAsset.getServerUpdatedAt(),
			    serverAsset.getMd5Hash(),
			    id);

	    if (!AssetHashUtils.md5AssetHash(localAsset).equals(serverAsset.getMd5Hash())) {
		log.debug("Updating asset content...");
		Path path = localAsset.getLocalPath();
		Files.createDirectories(path.getParent());
		try (InputStream in = serverAsset.getContent(); OutputStream out = new BufferedOutputStream(
				new FileOutputStream(path.toFile()))) {
		    IOUtils.copy(in, out);
		}
	    }
	    if (!serverAsset.getLocalPath().equals(localAsset.getLocalPath())) {
		log.debug("Moving asset [{} -> {}]...", localAsset.getLocalPath(), serverAsset.getLocalPath());
		Files.createDirectories(serverAsset.getLocalPath().getParent());
		Files.move(localAsset.getLocalPath(), serverAsset.getLocalPath());
	    }
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public ServerAsset actionResource(Context context, Long id) {
	return context.serverAssets().get(id);
    }
}
