package ru.chaykin.wjss.action;

import ru.chaykin.wjss.calc.PageChange;
import ru.chaykin.wjss.context.Context;

public interface IChangeTypeAction {

    void execute(Context context, PageChange pageChange);
}
