package ru.chaykin.wjss.context;

import java.sql.Connection;

import ru.chaykin.wjss.graphql.api.ClientApi;

public record Context(Connection connection, ClientApi api) {
}
