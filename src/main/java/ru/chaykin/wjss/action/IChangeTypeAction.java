package ru.chaykin.wjss.action;

import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;
import ru.chaykin.wjss.data.IPage;

public interface IChangeTypeAction {

    void execute(Context context, IPage page);
}
