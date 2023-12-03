package ru.chaykin.wjss.command.conflict;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResolveType {
    MANUAL("m", "(m)anual"),
    OURS("o", "(o)urs"),

    THEIRS("t", "(t)heirs");

    public final String key;

    @Getter
    public final String label;

    public static ResolveType of(String key) {
	return Stream.of(values()).filter(t -> t.key.equals(key)).findAny().orElse(null);
    }
}
