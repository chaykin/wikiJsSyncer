package ru.chaykin.wjss.calc.asset;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.data.asset.LocalAsset;

@RequiredArgsConstructor
public class LocalAssetFetcher {
    private final Connection connection;

    public Map<Long, LocalAsset> fetch() {
	try (var statement = connection.prepareStatement("SELECT * FROM assets")) {
	    Map<Long, LocalAsset> assets = new HashMap<>();

	    ResultSet rs = statement.executeQuery();
	    while (rs.next()) {
		LocalAsset asset = new LocalAsset(rs);
		assets.put(asset.getId(), asset);
	    }

	    return assets;
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}
    }
}