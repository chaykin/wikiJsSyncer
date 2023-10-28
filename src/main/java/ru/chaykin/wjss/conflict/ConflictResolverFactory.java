package ru.chaykin.wjss.conflict;

import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import ru.chaykin.wjss.conflict.impl.MineConflictResolver;
import ru.chaykin.wjss.conflict.impl.PostponeConflictResolver;
import ru.chaykin.wjss.conflict.impl.TheirsConflictResolver;
import ru.chaykin.wjss.context.Context;

@RequiredArgsConstructor
public class ConflictResolverFactory {
    private final ResolveType forceResolveType;

    public ConflictResolver createResolver(Context context) {
	return switch (readUserSelection()) {
	    case MINE -> new MineConflictResolver(context);
	    case THEIRS -> new TheirsConflictResolver(context);
	    case POSTPONE -> new PostponeConflictResolver(context);
	};
    }

    private ResolveType readUserSelection() {
	String selectOptions = Stream.of(ResolveType.values())
			.map(ResolveType::getLabel)
			.collect(Collectors.joining(", "));
	System.out.printf("Conflict discovered. Select: %s%n", selectOptions);

	if (forceResolveType != null) {
	    return forceResolveType;
	}

	Scanner userInputScanner = new Scanner(System.in);
	while (true) {
	    String key = userInputScanner.nextLine();
	    ResolveType resolveType = ResolveType.of(key);
	    if (resolveType != null) {
		return resolveType;
	    }
	}
    }
}
