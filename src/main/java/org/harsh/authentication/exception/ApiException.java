package org.harsh.authentication.exception;


public class ApiException extends Exception {
    ErrorCodes error;
    String msg;

    public ApiException(String message, ErrorCodes error) {
        this.error = error;
        this.msg = message;
    }

    public ErrorCodes getError() {
        return this.error;
    }

    public String getMsg() {
        return this.msg;
    }
}
