package ru.chaykin.wjss.action.impl.asset.incoming;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.HttpResponseException;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.action.impl.asset.IAssetChangeTypeAction;
import ru.chaykin.wjss.change.asset.AssetChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.data.asset.LocalAsset;
import ru.chaykin.wjss.data.asset.ServerAsset;
import ru.chaykin.wjss.db.DatabaseUtils;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

@Log4j2
public class IncomingNewChangeTypeAction
		implements IAssetChangeTypeAction, IChangeTypeAction<LocalAsset, ServerAsset, IAsset, AssetChange> {
    private static final String INSERT_ASSET_QUERY = """
		    INSERT INTO assets(
		    		id, folderId, remote_path, local_path,
		    		content_type, remote_update_at, md5_hash)
		    	VALUES(?,?,?,?,?,?,?)""";

    @Override
    public void execute(Context context, Long id) {
	IAsset asset = context.serverAssets().get(id);
	log.debug("Creating new local asset: {}", asset.getRemotePath());

	try {
	    Files.createDirectories(asset.getLocalPath().getParent());
	    createAssetFile(asset);

	    DatabaseUtils.update(context.connection(), INSERT_ASSET_QUERY,
			    asset.getId(),
			    asset.getFolderId(),
			    asset.getRemotePath(),
			    asset.getLocalPath().toString(),
			    asset.getContentType(),
			    asset.getServerUpdatedAt(),
			    asset.getMd5Hash());
	} catch (HttpResponseException e) {
	    if (e.getStatusCode() == HTTP_NOT_FOUND) {
		log.warn("Could not download asset %s".formatted(asset.getRemotePath()), e);
		return;
	    }
	    throw new RuntimeException(e);
	} catch (IOException | SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    private void createAssetFile(IAsset asset) throws IOException {
	try (InputStream content = asset.getContent();
	     OutputStream out = new BufferedOutputStream(new FileOutputStream(asset.getLocalPath().toFile()))) {
	    IOUtils.copy(content, out);
	}
    }
}
