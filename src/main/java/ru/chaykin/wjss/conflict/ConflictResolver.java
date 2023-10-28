package ru.chaykin.wjss.conflict;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;

@RequiredArgsConstructor
public abstract class ConflictResolver {
    protected final Context context;

    public abstract void resolve(PageChange pageChange);
}
