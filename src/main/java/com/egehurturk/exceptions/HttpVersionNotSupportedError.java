package com.egehurturk.exceptions;

public class HttpVersionNotSupportedError extends Exception {
    public HttpVersionNotSupportedError(String errMsg) {
        super(errMsg);
    }

    public HttpVersionNotSupportedError(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
}
