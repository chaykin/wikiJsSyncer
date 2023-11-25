package ru.chaykin.wjss.data;

import java.nio.file.Files;

public interface ILocalResource extends IResource {

    default boolean exists() {
	return Files.exists(getLocalPath());
    }
}
