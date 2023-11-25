package ru.chaykin.wjss.graphql.mutation;

import java.util.Date;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import ru.chaykin.wjss.data.page.IPage;
import ru.chaykin.wjss.graphql.api.ClientApi;

@RequiredArgsConstructor
public class UpdatePageMutation {
    private static final String UPDATE_MUTATION = """
		    mutation {
		    	pages {
		    		update(id: %s, content: "%s", tags: [%s], isPublished: true) {
		    			responseResult { succeeded errorCode slug message }
		    			page { updatedAt }
		    		}
		    	}
		    }""";

    private final ClientApi api;

    public Date updatePage(IPage page) {
	String tags = page.getTags().stream().map(t -> String.format("\"%s\"", t)).collect(Collectors.joining(","));
	String content = StringEscapeUtils.escapeJava(page.getContent());
	String mutation = String.format(UPDATE_MUTATION, page.getId(), content, tags);

	//noinspection uncheckeds
	Type.Data.Pages.Update update = api.mutation(Type.class, mutation).data().pages().update();
	if (update.responseResult().succeeded()) {
	    return update.page().updatedAt();
	}

	throw new RuntimeException("Could not update page " + page.getId() + ". Response: " + update.responseResult());
    }

    private record Type(Data data) {
	private record Data(Pages pages) {
	    private record Pages(Update update) {
		private record Update(ResponseResult responseResult, Page page) {
		    private record ResponseResult(boolean succeeded, int errorCode, String slug, String message) {
		    }

		    private record Page(Date updatedAt) {
		    }
		}
	    }
	}
    }
}
