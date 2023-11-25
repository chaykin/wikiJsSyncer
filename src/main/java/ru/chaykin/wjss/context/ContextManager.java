package ru.chaykin.wjss.context;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableObject;
import ru.chaykin.wjss.db.DatabaseManager;
import ru.chaykin.wjss.graphql.api.ClientApi;

public class ContextManager {
    private static final ClientApi API = new ClientApi();

    public <T> T apply(Function<Context, T> contextFunc) {
	MutableObject<T> result = new MutableObject<>();
	execute(context -> result.setValue(contextFunc.apply(context)));

	return result.getValue();
    }

    public void execute(IContextExecutor executor) {
	new DatabaseManager().execute(c -> executor.execute(Context.createBuilder(c, API).build()));
    }

    public interface IContextExecutor {
	void execute(Context context) throws RuntimeException;
    }
}
