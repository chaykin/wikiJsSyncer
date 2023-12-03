package ru.chaykin.wjss.command;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.chaykin.wjss.command.conflict.ResolveConflictCommand;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandFactory {

    public static Map<String, BaseCommand> createCommands() {
	return Stream.of(new SyncCommand(), new ResolveConflictCommand(), new StatusCommand())
			.collect(Collectors.toMap(BaseCommand::getName, Function.identity()));
    }
}
