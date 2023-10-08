package ru.chaykin.wjss;

import ru.chaykin.wjss.action.SyncAction;
import ru.chaykin.wjss.context.ContextManager;

public class App {

    public static void main(String[] args) {
	new ContextManager().execute(context -> new SyncAction(context).execute());
    }
}
