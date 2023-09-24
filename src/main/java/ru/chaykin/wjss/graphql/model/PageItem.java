package ru.chaykin.wjss.graphql.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record PageItem(Integer id, String path, String hash, String content, Date updatedAt) {
}
