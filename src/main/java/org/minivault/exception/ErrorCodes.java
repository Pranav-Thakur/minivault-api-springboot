package org.minivault.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    INVALID_INPUT("SE-00", "invalid input"),
    INTERNAL_SERVER_ERROR("SE-01", "server down");

    private final String code;
    private final String msg;

    ErrorCodes(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
