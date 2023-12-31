package ru.chaykin.wjss.auth;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.config.ApplicationConfig;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.model.auth.AuthInfo;
import ru.chaykin.wjss.graphql.model.auth.AuthStrategy;
import ru.chaykin.wjss.graphql.mutation.auth.AuthMutation;
import ru.chaykin.wjss.graphql.query.auth.AuthQuery;

@Log4j2
public class AuthTokenProvider {

    public String getAuthToken(ClientApi api) {
	String user = ApplicationConfig.get("auth.user");
	String password = ApplicationConfig.get("auth.password");
	String strategyType = ApplicationConfig.get("auth.type");
	log.debug("Getting {} auth token for {}", strategyType, user);

	AuthQuery authQuery = new AuthQuery(api);
	AuthStrategy authStrategy = authQuery.getActiveStrategies().stream()
			.filter(s -> strategyType.equals(s.type()))
			.findAny().orElseThrow();

	AuthMutation authMutation = new AuthMutation(api);
	AuthInfo authInfo = authMutation.auth(user, password, authStrategy.key());
	if (!authInfo.succeeded()) {
	    throw new RuntimeException("Could not get token: " + authInfo);
	}

	log.debug("Got auth token successfully");
	return authInfo.token();
    }
}
