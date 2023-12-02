package ru.chaykin.wjss.change;

public enum ChangeType {
    NEW,
    UPDATED,
    DELETED,

    @Deprecated(forRemoval = true)
    REMOTE_NEW,

    @Deprecated(forRemoval = true)
    REMOTE_UPDATED,

    @Deprecated(forRemoval = true)
    REMOTE_DELETED,

    @Deprecated(forRemoval = true)
    LOCAL_UPDATED,

    @Deprecated(forRemoval = true)
    LOCAL_DELETED
}
