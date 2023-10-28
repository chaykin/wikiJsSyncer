package ru.chaykin.wjss.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Inspired by <a href="https://github.com/EMCECS/ssl-certificate-extractor">SSL Server Certificate Utility</a> project
 */
public class SSLCertLoader {
    private SSLCertLoader() {
    }

    public static X509Certificate[] loadServerCertChain(String server) {
	ServerAddress serverAddr = new ServerAddress(server);
	ServerAlwaysTrustManager trustManager = new ServerAlwaysTrustManager();

	try (Socket socket = createSocket(trustManager, serverAddr); OutputStream out = socket.getOutputStream()) {
	    out.write("GET / HTTP/1.1\n\n".getBytes());
	} catch (IOException | GeneralSecurityException e) {
	    throw new RuntimeException("Could not load certificates from " + server, e);
	}

	return trustManager.getAcceptedIssuers();
    }

    private static Socket createSocket(TrustManager trustManager, ServerAddress addr)
		    throws GeneralSecurityException, IOException {
	SSLContext context = SSLContext.getInstance("TLS");
	context.init(null, new TrustManager[] { trustManager }, null);

	return context.getSocketFactory().createSocket(addr.host, addr.port);
    }
}
