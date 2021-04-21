package com.egehurturk.exceptions;

public class BadRequest400Exception extends HttpRequestException {


    public BadRequest400Exception(String errMsg, int code, String message) {
        super(errMsg);
        this.code = code;
        this.message = message;
    }

    public BadRequest400Exception(String errMsg, Throwable cause, int code, String message) {
        super(errMsg, cause);
        this.code = code;
        this.message = message;
    }
}
