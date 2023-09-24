package ru.chaykin.wjss.graphql.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record PageListItem(Integer id, String path, String locale, String title, String description, String contentType,
			   Date updatedAt, List<String> tags) {
}
