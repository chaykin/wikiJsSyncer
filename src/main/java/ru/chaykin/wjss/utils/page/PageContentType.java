package ru.chaykin.wjss.utils.page;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PageContentType {
    MARKDOWN("markdown", "md"),
    UNKNOWN("*", "txt");

    private final String typeName;
    private final String extension;

    public static PageContentType of(String typeName) {
	return Stream.of(values()).filter(t -> t.typeName.equals(typeName)).findAny().orElse(UNKNOWN);
    }
}
