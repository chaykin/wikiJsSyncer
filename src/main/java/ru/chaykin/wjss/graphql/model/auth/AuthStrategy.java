package ru.chaykin.wjss.graphql.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record AuthStrategy(String type, String key) {
}
