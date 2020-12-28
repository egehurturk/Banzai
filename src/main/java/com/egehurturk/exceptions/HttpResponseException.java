package com.egehurturk.exceptions;

public class HttpResponseException extends Exception{
    public HttpResponseException(String errMsg) {
        super(errMsg);
    }

    public HttpResponseException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
}
