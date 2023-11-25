package ru.chaykin.wjss.data.asset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.chaykin.wjss.data.IResource;

public interface IAsset extends IResource {

    long getFolderId();

    InputStream getContent() throws IOException;
}
