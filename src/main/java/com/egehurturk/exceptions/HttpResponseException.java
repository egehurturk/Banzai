package com.egehurturk.exceptions;

public abstract class HttpResponseException extends Exception{
    public HttpResponseException(String errMsg) {
        super(errMsg);
    }

    public HttpResponseException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
}
