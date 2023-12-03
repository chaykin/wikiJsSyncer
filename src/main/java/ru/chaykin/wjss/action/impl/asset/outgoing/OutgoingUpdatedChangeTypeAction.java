package ru.chaykin.wjss.action.impl.asset.outgoing;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.action.IChangeTypeAction;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.db.DatabaseUtils;
import ru.chaykin.wjss.graphql.query.AssetListQuery;
import ru.chaykin.wjss.utils.asset.AssetHashUtils;

@Log4j2
public class OutgoingUpdatedChangeTypeAction implements IChangeTypeAction {
    private static final String UPDATE_ASSET_QUERY = """
		    UPDATE assets SET
		    	server_update_at = ?,
		    	md5_hash = ?
		    WHERE id = ?""";

    @Override
    public void execute(Context context, Long id) {
	IAsset asset = context.localAssets().get(id);

	log.debug("Uploading updates to server asset: {}", asset);

	Date updatedAt = uploadAsset(context, asset);
	try {
	    DatabaseUtils.update(context.connection(), UPDATE_ASSET_QUERY,
			    updatedAt.getTime(),
			    AssetHashUtils.md5AssetHash(asset),
			    id);
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    private Date uploadAsset(Context context, IAsset asset) {
	try (InputStream in = asset.getContent()) {
	    long folderId = asset.getFolderId();
	    String contentType = asset.getContentType();
	    String fileName = asset.getLocalPath().getFileName().toString();
	    context.api().uploadAsset(folderId, in, contentType, fileName);
	} catch (IOException e) {
	    throw new RuntimeException("Could not read asset %s content".formatted(asset));
	}

	return fetchUpdatedAt(context, asset);
    }

    private Date fetchUpdatedAt(Context context, IAsset asset) {
	return new AssetListQuery(context.api()).fetchAssets(asset.getFolderId()).stream()
			.filter(a -> a.id() == asset.getFolderId())
			.map(AssetListQuery.Asset::updatedAt).findAny()
			.orElseThrow(() -> new RuntimeException("Could not find updated asset %s".formatted(asset)));
    }
}
