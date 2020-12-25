package com.egehurturk.exceptions;

/**
 * Custom exception for Http Request lifecycle exceptions.
 */
public class HttpRequestException extends Exception {
    public HttpRequestException(String errMsg) {
        super(errMsg);
    }

    public HttpRequestException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }

}
