package ru.chaykin.wjss.context;

import ru.chaykin.wjss.db.DatabaseManager;
import ru.chaykin.wjss.graphql.api.ClientApi;

public class ContextManager {

    public void execute(IContextExecutor executor) {
	new DatabaseManager().execute(c -> executor.execute(new Context(c, new ClientApi())));
    }

    public interface IContextExecutor {
	void execute(Context context) throws RuntimeException;
    }
}
