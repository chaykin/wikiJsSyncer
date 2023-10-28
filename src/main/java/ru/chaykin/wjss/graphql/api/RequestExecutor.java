package ru.chaykin.wjss.graphql.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.net.ssl.SSLHandshakeException;

import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import ru.chaykin.wjss.config.ApplicationConfig;

@Log4j2
public class RequestExecutor {
    private RequestExecutor() {
    }

    private static CloseableHttpClient httpClient = createHttpClient();
    private static boolean useServerKeystore = Files.exists(getCacerts());
    private static boolean retry;

    public static Response execute(Request request) throws IOException {
	try {
	    return request.execute(httpClient);
	} catch (SSLHandshakeException e) {
	    log.debug("Problem with certificates...");

	    if (!useServerKeystore) {
		log.debug("Try to accept server certificate(s)...");
		useServerKeystore = true;

		httpClient = createHttpClient();
		return execute(request);
	    } else if (!retry) {
		log.debug("Cached certificate(s) is outdated. Try to accept new one...");
		retry = true;
		Files.delete(getCacerts());

		httpClient = createHttpClient();
		return execute(request);
	    }

	    throw e;
	}
    }

    private static CloseableHttpClient createHttpClient() {
	Path cacerts = getCacerts();
	if (Files.exists(cacerts) || useServerKeystore) {
	    String server = ApplicationConfig.get("wiki.js.server");
	    return HttpClientFactory.createWithServerKeystore(server, cacerts);
	}

	return HttpClientFactory.createDefault();
    }

    private static Path getCacerts() {
	return Path.of(ApplicationConfig.get("cacerts"));
    }
}
