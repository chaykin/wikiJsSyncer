package ru.chaykin.wjss.utils;

import java.nio.file.Path;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.chaykin.wjss.config.ApplicationConfig;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathUtils {
    public static final String REPO_PATH = ApplicationConfig.get("wiki.js.repo");

    public static String appendToFileName(Path path, String postfix) {
	String fullPath = path.toString();
	int index = fullPath.lastIndexOf(".");
	if (index >= 0) {
	    String name = fullPath.substring(0, index);
	    String ext = fullPath.substring(index);

	    return name + "." + postfix + ext;
	}

	return fullPath + "." + postfix;
    }
}
