package com.egehurturk.exceptions;


public class NotFound404Exception extends HttpRequestException {
    public int code;
    public String message;


    public NotFound404Exception(String errMsg, int code, String message) {
        super(errMsg);
        this.code = code;
        this.message = message;
    }

    public NotFound404Exception(String errMsg, Throwable cause, int code, String message) {
        super(errMsg, cause);
        this.code = code;
        this.message = message;
    }
}
