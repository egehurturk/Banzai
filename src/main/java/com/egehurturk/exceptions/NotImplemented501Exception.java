package com.egehurturk.exceptions;

public class NotImplemented501Exception extends HttpResponseException{
    public int code;
    public String message;


    public NotImplemented501Exception(String errMsg, int code, String message) {
        super(errMsg);
        this.code = code;
        this.message = message;
    }

    public NotImplemented501Exception(String errMsg, Throwable cause, int code, String message) {
        super(errMsg, cause);
        this.code = code;
        this.message = message;
    }
}
