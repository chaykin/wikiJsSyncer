package ru.chaykin.wjss.graphql.mutation;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.graphql.api.ClientApi;

@RequiredArgsConstructor
public class DeletePageMutation {
    private static final String UPDATE_MUTATION = """
		    mutation {
		    	pages {
		    		delete(id: %s) {
		    			responseResult { succeeded errorCode slug message }
		    		}
		    	}
		    }""";

    private final ClientApi api;

    public void deletePage(IPage page) {
	String mutation = String.format(UPDATE_MUTATION, page.getId());

	//noinspection uncheckeds
	var result = api.mutation(Type.class, mutation).data().pages().delete().responseResult();
	if (!result.succeeded()) {
	    throw new RuntimeException("Could not delete page " + page.getId() + ". Response: " + result);
	}
    }

    private record Type(Data data) {
	private record Data(Pages pages) {
	    private record Pages(Delete delete) {
		private record Delete(ResponseResult responseResult) {
		    private record ResponseResult(boolean succeeded, int errorCode, String slug, String message) {
		    }
		}
	    }
	}
    }
}