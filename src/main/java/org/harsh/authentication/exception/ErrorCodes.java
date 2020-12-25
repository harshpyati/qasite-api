package org.harsh.authentication.exception;

public enum ErrorCodes {
    RESOURCE_NOT_FOUND(404), INTERNAL_EXCEPTION(500), INVALID_ARGUMENT(400), SUCCESS(200), SUCCESS_NO_CONTENT(204);
    int code;

    ErrorCodes(int code) {
        this.code = code;
    }
}
