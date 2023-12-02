package ru.chaykin.wjss.graphql.mutation;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.asset.IAsset;
import ru.chaykin.wjss.graphql.api.ClientApi;

@RequiredArgsConstructor
public class DeleteAssetMutation {
    private static final String UPDATE_MUTATION = """
		    mutation {
		    	assets {
		    		deleteAsset(id: %s) {
		    			responseResult { succeeded errorCode slug message }
		    		}
		    	}
		    }""";

    private final ClientApi api;

    public void deleteAsset(IAsset asset) {
	String mutation = String.format(UPDATE_MUTATION, asset.getId());

	//noinspection uncheckeds
	var result = api.mutation(Type.class, mutation).data().assets().deleteAsset().responseResult();
	if (!result.succeeded()) {
	    throw new RuntimeException("Could not delete asset " + asset.getId() + ". Response: " + result);
	}
    }

    private record Type(Data data) {
	private record Data(Assets assets) {
	    private record Assets(DeleteAsset deleteAsset) {
		private record DeleteAsset(ResponseResult responseResult) {
		}
	    }
	}
    }
}
