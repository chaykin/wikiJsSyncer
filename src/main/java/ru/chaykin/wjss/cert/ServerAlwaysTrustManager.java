package ru.chaykin.wjss.cert;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class ServerAlwaysTrustManager implements X509TrustManager {
    private X509Certificate[] serverCertChain;

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
	// Noting to do
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
	serverCertChain = chain;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
	return serverCertChain;
    }
}