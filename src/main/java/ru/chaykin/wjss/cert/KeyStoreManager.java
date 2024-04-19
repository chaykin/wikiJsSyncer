package ru.chaykin.wjss.cert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import lombok.extern.log4j.Log4j2;
import ru.chaykin.wjss.option.AppOptions;

@Log4j2
public class KeyStoreManager {
    private KeyStoreManager() {
    }

    public static KeyStore createKeyStoreForServer(String server, Path cacerts) {
	try {
	    KeyStore keyStore = createKeyStore();
	    if (Files.exists(cacerts)) {
		log.debug("Using keystore {}", cacerts);

		try (InputStream in = Files.newInputStream(cacerts)) {
		    keyStore.load(in, new char[0]);
		}
		return keyStore;
	    }

	    log.debug("Loading certificate(s) from server {}", server);
	    keyStore.load(null, new char[0]);
	    addServerCerts(keyStore, server);

	    log.debug("Creating keystore {}", cacerts);
	    try (OutputStream out = Files.newOutputStream(cacerts)) {
		keyStore.store(out, new char[0]);
	    }
	    return keyStore;
	} catch (GeneralSecurityException | IOException e) {
	    throw new RuntimeException("Could not create key store for server " + server, e);
	}
    }

    private static KeyStore createKeyStore() throws KeyStoreException {
	// Create a new trust store, use getDefaultType for .jks files or "pkcs12" for .p12 files
	return KeyStore.getInstance(KeyStore.getDefaultType());
    }

    private static void addServerCerts(KeyStore keyStore, String server) throws GeneralSecurityException {
	for (X509Certificate serverCert : SSLCertLoader.loadServerCertChain(server)) {
	    acquireUserConfirm(serverCert);
	    keyStore.setCertificateEntry(serverCert.getSubjectX500Principal().getName(), serverCert);
	}
    }

    private static void acquireUserConfirm(X509Certificate serverCert) {
	if (!AppOptions.getOptions().isAlwaysCertTrust()) {
	    System.out.println("Java doesn't have this certificate as a trusted:");
	    System.out.println(serverCert);

	    Scanner userInputScanner = new Scanner(System.in);
	    while (true) {
		System.out.println("Do You trust this certificate? [y/n]");
		String key = userInputScanner.nextLine();
		if (key.equalsIgnoreCase("y")) {
		    return;
		} else if (key.equalsIgnoreCase("n")) {
		    System.exit(5);
		}
	    }
	}
    }
}
