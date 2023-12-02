package ru.chaykin.wjss.graphql.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.net.URIBuilder;
import ru.chaykin.wjss.auth.AuthTokenProvider;
import ru.chaykin.wjss.config.ApplicationConfig;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;

@Log4j2
public class ClientApi {
    private static final String ASSET_ENDPOINT = ApplicationConfig.get("wiki.js.server");
    private static final String ENDPOINT = ApplicationConfig.get("wiki.js.graphql.endpoint");

    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthTokenProvider tokenProvider = new AuthTokenProvider();
    private String authToken;

    public <T> T query(Class<T> type, String query) {
	log.debug("Execute query: {}", query);
	try {
	    Request request = Request.get(createLocation(query));
	    return executeRequest(type, request);
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Invalid request url", e);
	}
    }

    public <T> T mutation(Class<T> type, String query) {
	log.debug("Execute mutation: {}", query);

	String body = String.format("{\"query\": \"%s\"}", StringEscapeUtils.escapeJava(query));

	Request request = Request.post(ENDPOINT).bodyString(body, APPLICATION_JSON);
	return executeRequest(type, request);
    }

    public void downloadAsset(String path, File destination) throws IOException {
	log.debug("Download asset: {}", path);
	try {
	    URI uri = new URIBuilder(URI.create(ASSET_ENDPOINT)).appendPath(path).build();
	    Request request = Request.get(uri);

	    String token = getAuthToken();
	    if (StringUtils.isNotBlank(token)) {
		request = request.addHeader("Authorization", "Bearer " + token);
	    }

	    Response response = RequestExecutor.execute(request);
	    response.saveContent(destination);
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Invalid url: " + path, e);
	}
    }

    public void uploadAsset(long folderId, InputStream content, String contentType, String fileName) {
	log.debug("Upload asset {} to: {}", fileName, folderId);
	try {
	    Request request = Request.post(ASSET_ENDPOINT)
			    .body(MultipartEntityBuilder
					    .create()
					    .addTextBody("mediaUpload", "{\"folderId\": %s}".formatted(folderId))
					    .addBinaryBody("mediaUpload", content, ContentType.create(contentType),
							    fileName)
					    .build());

	    String token = getAuthToken();
	    if (StringUtils.isNotBlank(token)) {
		request = request.addHeader("Authorization", "Bearer " + token);
	    }

	    Response response = RequestExecutor.execute(request);
	    if (response.returnResponse().getCode() != HTTP_OK) {
		throw new RuntimeException("Could not upload assetv %s to %s".formatted(fileName, folderId));
	    }
	} catch (IOException e) {
	    throw new RuntimeException("Failed to execute request", e);
	}
    }

    private <T> T executeRequest(Class<T> type, Request request) {
	try {
	    String token = getAuthToken();
	    if (StringUtils.isNotBlank(token)) {
		request = request.addHeader("Authorization", "Bearer " + token);
	    }
	    Response response = RequestExecutor.execute(request);

	    byte[] content = response.returnContent().asBytes();
	    log.trace("Raw response: {}", () -> new String(content));

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
