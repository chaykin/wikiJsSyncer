package ru.chaykin.wjss.graphql.query;

import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.graphql.api.ClientApi;

@RequiredArgsConstructor
public class AssetListQuery {
    private static final String ASSETS_QUERY = """
		    {
		       assets {
		         list(folderId: %s, kind: ALL) {
		           id filename ext mime fileSize updatedAt
		         }
		       }
		     }""";

    private final ClientApi api;

    public List<Asset> fetchAssets(long folderId) {
	String query = String.format(ASSETS_QUERY, folderId);
	//noinspection uncheckeds
	return api.query(Type.class, query).data().assets().list();
    }

    public record Asset(long id, String filename, String ext, String mime, long fileSize, Date updatedAt) {
    }

    private record Type(Data data) {
	private record Data(Assets assets) {
	    private record Assets(List<Asset> list) {
	    }
	}
    }
}
