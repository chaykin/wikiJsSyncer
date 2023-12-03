package ru.chaykin.wjss.data.page;

import java.util.List;

import ru.chaykin.wjss.data.IResource;

public interface IPage extends IResource {

    String getTitle();

    String description();

    String getLocale();

    String getContent();

    List<String> getTags();
}
