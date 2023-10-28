package ru.chaykin.wjss.cert;

public class ServerAddress {
    private static final int DEFAULT_PORT = 443;

    public final String host;
    public final int port;

    public ServerAddress(String server) {
	String[] parts = server.replace("https://", "").split(":");

	host = parts[0];
	port = parts.length > 1 ? Integer.parseInt(parts[1]) : DEFAULT_PORT;
    }
}
