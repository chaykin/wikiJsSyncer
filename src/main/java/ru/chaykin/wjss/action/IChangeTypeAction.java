package ru.chaykin.wjss.action;

import ru.chaykin.wjss.change.ResourceChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.ILocalResource;
import ru.chaykin.wjss.data.IRemoteResource;
import ru.chaykin.wjss.data.IResource;

public interface IChangeTypeAction<L extends ILocalResource, R extends IRemoteResource,
		C extends IResource, RC extends ResourceChange<L, R, C>> {

    void execute(Context context, RC resourceChange);
}
