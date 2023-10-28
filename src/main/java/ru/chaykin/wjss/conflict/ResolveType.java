package ru.chaykin.wjss.conflict;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResolveType {
    POSTPONE("p", "(p)ostpone"),
    MINE("m", "(m)ine"),
    THEIRS("t", "(t)heirs");

    public final String key;

    @Getter
    public final String label;

    public static ResolveType of(String key) {
	return Stream.of(values()).filter(t -> t.key.equals(key)).findAny().orElse(null);
    }
}
