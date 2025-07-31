package ru.chaykin.wjss.graphql.mutation.auth;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.model.auth.AuthInfo;

@RequiredArgsConstructor
public class AuthMutation {
    private static final String AUTH_MUTATION = """
		    mutation {
		    	authentication {
		    		login(username: "%s", password: "%s", strategy: "%s") {
		    			responseResult { succeeded errorCode slug message }
		    			jwt
		    		}
		    	}
		    }""";

    private final ClientApi api;

    public AuthInfo auth(String user, String password, String strategy) {
	String mutation = String.format(AUTH_MUTATION, user, StringEscapeUtils.escapeJava(password), strategy);

	//noinspection uncheckeds
	Login login = api.mutation(Type.class, mutation).data().authentication().login();
	ResponseResult res = login.responseResult();

	return new AuthInfo(res.succeeded(), res.errorCode(), res.slug(), res.message(), login.jwt());
    }

    private record ResponseResult(boolean succeeded, int errorCode, String slug, String message) {
    }

    private record Login(ResponseResult responseResult, String jwt) {
    }

    private record Authentication(Login login) {
    }

    private record Data(Authentication authentication) {
    }

    private record Type(Data data) {
    }
}
