package ru.chaykin.wjss.graphql.api;

import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import ru.chaykin.wjss.cert.KeyStoreManager;

public class HttpClientFactory {
    private HttpClientFactory() {
    }

    public static CloseableHttpClient createDefault() {
	return create(null, null);
    }

    public static CloseableHttpClient createWithServerKeystore(String server, Path cacerts) {
	return create(server, cacerts);
    }

    private static CloseableHttpClient create(String server, Path cacerts) {
	try {
	    return HttpClientBuilder.create()
			    .setConnectionManager(createConnectionManager(server, cacerts))
			    .useSystemProperties()
			    .evictExpiredConnections()
			    .evictIdleConnections(TimeValue.ofMinutes(1))
			    .build();
	} catch (GeneralSecurityException e) {
	    throw new RuntimeException("Could not create httpClient", e);
	}
    }

    private static HttpClientConnectionManager createConnectionManager(String server, Path cacerts)
		    throws GeneralSecurityException {
	PoolingHttpClientConnectionManagerBuilder builder = PoolingHttpClientConnectionManagerBuilder.create()
			.setMaxConnPerRoute(100)
			.setMaxConnTotal(200)
			.setDefaultConnectionConfig(ConnectionConfig.custom().
					setValidateAfterInactivity(TimeValue.ofSeconds(10))
					.build());
	if (StringUtils.isBlank(server)) {
	    builder.useSystemProperties();
	} else {
	    builder.setSSLSocketFactory(createSocketFactory(server, cacerts));
	}

	return builder.build();
    }

    private static LayeredConnectionSocketFactory createSocketFactory(String server, Path cacerts)
		    throws GeneralSecurityException {
	KeyStore keyStore = KeyStoreManager.createKeyStoreForServer(server, cacerts);
	var context = SSLContexts.custom().loadTrustMaterial(keyStore, null).build();
	return SSLConnectionSocketFactoryBuilder.create().setSslContext(context).build();
    }
}
