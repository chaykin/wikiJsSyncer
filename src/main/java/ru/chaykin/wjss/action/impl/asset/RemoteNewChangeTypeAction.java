package ru.chaykin.wjss.action.impl.asset;

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
import ru.chaykin.wjss.change.asset.AssetChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.db.DatabaseUtils;

@Log4j2
public class RemoteNewChangeTypeAction implements IAssetChangeTypeAction {
    private static final String INSERT_ASSET_QUERY = """
		    INSERT INTO assets(
		    		id, folderId, remote_path, local_path,
		    		content_type, remote_update_at, md5_hash)
		    	VALUES(?,?,?,?,?,?,?)""";

    @Override
    public void execute(Context context, AssetChange assetChange) {
	IAsset asset = assetChange.getRemoteResource();
	log.debug("Creating new local asset: {}", asset);

	try {
	    Path localPath = asset.getLocalPath();
	    Files.createDirectories(localPath.getParent());
	    try (InputStream content = asset.getContent(); OutputStream out = new BufferedOutputStream(
			    new FileOutputStream(localPath.toFile()))) {
		IOUtils.copy(content, out);
	    }

	    DatabaseUtils.update(context.connection(), INSERT_ASSET_QUERY,
			    asset.getId(),
			    asset.getFolderId(),
			    asset.getRemotePath(),
			    asset.getLocalPath().toString(),
			    asset.getContentType(),
			    asset.getRemoteUpdatedAt(),
			    asset.getMd5Hash());
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
