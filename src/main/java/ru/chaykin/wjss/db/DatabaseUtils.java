package ru.chaykin.wjss.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class DatabaseUtils {

    public static int update(Connection connection, String query, Object... params) throws SQLException {
	try (var statement = connection.prepareStatement(query)) {
	    prepareParams(statement, params);
	    return statement.executeUpdate();
	}
    }

    public static <T> Map<String, T> select(Connection connection, String query, Object... params) throws SQLException {
	try (var statement = connection.prepareStatement(query)) {
	    prepareParams(statement, params);

	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		System.out.println(rs.getInt("id") +  "\t" +
				rs.getString("name") + "\t" +
				rs.getDouble("capacity"));
	    }

	    return null;
	}
    }

    private static void prepareParams(PreparedStatement statement, Object... params) throws SQLException {
	for (int i = 0; i < params.length; i++) {
	    statement.setObject(i + 1, params[i]);
	}
    }
}
