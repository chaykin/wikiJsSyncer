package ru.chaykin.wjss.action;

import ru.chaykin.wjss.change.ResourceChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.data.IResource;
import ru.chaykin.wjss.data.IServerResource;

public interface IChangeTypeAction<L extends ILocalResource, R extends IServerResource,
		C extends IResource, RC extends ResourceChange<L, R, C>> {

    @Deprecated(forRemoval = true)
    default void execute(Context context, RC resourceChange) {
    }

    void execute(Context context, Long id);
}
