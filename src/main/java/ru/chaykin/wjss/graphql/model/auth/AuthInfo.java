package ru.chaykin.wjss.graphql.model.auth;

public record AuthInfo(boolean succeeded, int errorCode, String slug, String message, String token) {
}
