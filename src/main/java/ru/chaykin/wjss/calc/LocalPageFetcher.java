package ru.chaykin.wjss.calc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.LocalPage;

@RequiredArgsConstructor
public class LocalPageFetcher {
    private final Connection connection;

    public Map<Long, LocalPage> fetch() {
	try (var statement = connection.prepareStatement("SELECT * FROM pages")) {
	    Map<Long, LocalPage> pages = new HashMap<>();

	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		LocalPage page = new LocalPage(rs);
		pages.put(page.getId(), page);
	    }

	    return pages;
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}
