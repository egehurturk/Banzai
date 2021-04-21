package com.egehurturk.exceptions;

/**
 * Custom exception for Http Request lifecycle exceptions.
 */
public abstract class HttpRequestException extends Exception {
    public int code;
    public String message;

    public HttpRequestException(String errMsg) {
        super(errMsg);
    }

    public HttpRequestException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }

}
