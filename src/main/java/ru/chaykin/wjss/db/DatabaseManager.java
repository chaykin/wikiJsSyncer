package ru.chaykin.wjss.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import ru.chaykin.wjss.config.ApplicationConfig;

public class DatabaseManager {
    private static final String DB_URL = String.format("jdbc:sqlite:%s", ApplicationConfig.get("wiki.js.db"));

    public void execute(IDbExecutor executor) {
	try (Connection c = DriverManager.getConnection(DB_URL)) {
	    c.setAutoCommit(false);
	    executor.execute(c);
	    c.commit();
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }

    public interface IDbExecutor {
	void execute(Connection connection) throws SQLException;
    }
}
