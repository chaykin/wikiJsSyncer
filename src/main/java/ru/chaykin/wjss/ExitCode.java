package ru.chaykin.wjss;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExitCode {
    INVALID_PARAMS(1),
    INVALID_COMMAND(2),
    NOT_TRUSTED_CERT(5);

    public final int code;
}
