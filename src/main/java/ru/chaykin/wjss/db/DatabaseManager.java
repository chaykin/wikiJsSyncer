package ru.chaykin.wjss.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import ru.chaykin.wjss.App;
import ru.chaykin.wjss.config.ApplicationConfig;

import static java.nio.charset.StandardCharsets.UTF_8;

@Log4j2
public class DatabaseManager {
    private static final Path DB_PATH = Path.of(ApplicationConfig.get("wiki.js.db"));
    private static final String DB_URL = String.format("jdbc:sqlite:%s", DB_PATH);

    private boolean isInitialized;

    public void execute(IDbExecutor executor) {
	initDb();

	try (Connection c = DriverManager.getConnection(DB_URL)) {
	    c.setAutoCommit(false);
	    executor.execute(c);
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    private void initDb() {
	if (isInitialized) {
	    return;
	}
	isInitialized = true;

	if (Files.exists(DB_PATH)) {
	    return;
	}

	log.info("No database found at {}. Creating...", DB_PATH);

	try (InputStream in = App.class.getClassLoader().getResourceAsStream("db.sql")) {
	    String queries = IOUtils.toString(Objects.requireNonNull(in), UTF_8);

	    try (Connection c = DriverManager.getConnection(DB_URL)) {
		var statement = c.createStatement();
		for (String query : queries.split(";")) {
		    if (StringUtils.isNotBlank(query)) {
			statement.execute(query);
		    }
		}
	    }
	} catch (IOException | SQLException e) {
	    try {
		Files.deleteIfExists(DB_PATH);
	    } catch (IOException ex) {
		e.addSuppressed(ex);
	    }

	    throw new RuntimeException(e);
	}
    }

    public interface IDbExecutor {
	void execute(Connection connection) throws SQLException;
    }
}
