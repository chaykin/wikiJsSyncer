package ru.chaykin.wjss.graphql.mutation;

record ResponseResult(boolean succeeded, int errorCode, String slug, String message) {
}
