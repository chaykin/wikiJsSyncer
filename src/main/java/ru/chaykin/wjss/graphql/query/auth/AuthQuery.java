package ru.chaykin.wjss.graphql.query.auth;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.graphql.api.ClientApi;
import ru.chaykin.wjss.graphql.model.auth.AuthStrategy;

@RequiredArgsConstructor
public class AuthQuery {
    private static final String AUTH_STRATEGIES_QUERY = "{authentication{activeStrategies(enabledOnly: true){key strategy{key}}}}";

    private final ClientApi api;

    public List<AuthStrategy> getActiveStrategies() {
	//noinspection uncheckeds
	return api.query(Type.class, AUTH_STRATEGIES_QUERY).data().authentication().activeStrategies().stream()
			.map(as -> new AuthStrategy(as.strategy.key, as.key)).toList();
    }



    private record Strategy(String key) {
    }

    private record ActiveStrategy(String key, Strategy strategy) {
    }

    private record Authentication(List<ActiveStrategy> activeStrategies) {
    }

    private record Data(Authentication authentication) {
    }

    private record Type(Data data) {
    }
}
