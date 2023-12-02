package ru.chaykin.wjss.data;

import java.nio.file.Path;

public interface IResource {

    long getId();

    String getRemotePath();

    Path getLocalPath();

    String getContentType();

    String getMd5Hash();

    long getServerUpdatedAt();
}
