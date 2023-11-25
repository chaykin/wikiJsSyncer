package ru.chaykin.wjss.graphql.query;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.graphql.api.ClientApi;

@RequiredArgsConstructor
public class AssetFoldersQuery {
    private static final String ASSET_FOLDERS_QUERY = """
		    {
		       assets {
		         folders(parentFolderId: %s) {
		           id name
		         }
		       }
		     }""";

    private final ClientApi api;

    public List<AssetFolder> fetchAssetFolders(long parentFolderId) {
	String query = String.format(ASSET_FOLDERS_QUERY, parentFolderId);
	//noinspection uncheckeds
	return api.query(Type.class, query).data().assets().folders();
    }

    public record AssetFolder(long id, String name) {
    }

    private record Type(Data data) {
	private record Data(Assets assets) {
	    private record Assets(List<AssetFolder> folders) {
	    }
	}
    }
}
