package ru.chaykin.wjss.data;

import java.nio.file.Path;

public interface IResource {

    long getId();

    String getServerPath();

    Path getLocalPath();

    String getContentType();

    String getMd5Hash();

    long getServerUpdatedAt();
}
