package ru.chaykin.wjss.data;

import java.nio.file.Path;
import java.util.List;

public interface IPage {
    long getId();

    String getTitle();

    String description();

    String getLocale();

    String getRemotePath();

    Path getLocalPath();

    String getContentType();

    long getRemoteUpdatedAt();

    String getMd5Hash();

    String getContent();

    List<String> getTags();
}
