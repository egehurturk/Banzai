package com.egehurturk.exceptions;

public class MethodNotAllowedException extends HttpRequestException {
    public int code;
    public String message;


    public MethodNotAllowedException(String errMsg, int code, String message) {
        super(errMsg);
        this.code = code;
        this.message = message;
    }

    public MethodNotAllowedException(String errMsg, Throwable cause, int code, String message) {
        super(errMsg, cause);
        this.code = code;
        this.message = message;
    }
}
