package ru.chaykin.wjss.graphql.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.net.URIBuilder;
import ru.chaykin.wjss.auth.AuthTokenProvider;
import ru.chaykin.wjss.config.ApplicationConfig;

import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;

public class ClientApi {
    private static final String ENDPOINT = ApplicationConfig.get("wiki.js.graphql.endpoint");

    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthTokenProvider tokenProvider = new AuthTokenProvider();

    private String authToken;

    public <T> T query(Class<T> type, String query) {
	try {
	    Request request = Request.get(createLocation(query));
	    return executeRequest(type, request);
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Invalid request url", e);
	}
    }

    public <T> T mutation(Class<T> type, String query) {
	String body = String.format("{\"query\": \"%s\"}", query);

	Request request = Request.post(ENDPOINT).bodyString(body, APPLICATION_JSON);
	return executeRequest(type, request);
    }

    private <T> T executeRequest(Class<T> type, Request request) {
	try {
	    String token = getAuthToken();
	    if (StringUtils.isNotBlank(token)) {
		request = request.addHeader("Authorization", "Bearer " + token);
	    }
	    Response response = request.execute();

	    byte[] content = response.returnContent().asBytes();
	    return mapper.readValue(content, type);
	} catch (IOException e) {
	    throw new RuntimeException("Failed to execute request", e);
	}
    }

    private URI createLocation(String query) throws URISyntaxException {
	return new URIBuilder(ENDPOINT).addParameter("query", query).build();
    }

    private String getAuthToken() {
	if (authToken == null) {
	    authToken = ""; // для исключения рекурсивных вызовов
	    authToken = tokenProvider.getAuthToken(this);
	}

	return authToken;
    }
}
