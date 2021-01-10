package com.egehurturk.util;

public enum StatusEnum {
    _100_CONTINUE(100, "Continue"),
    _200_OK(200, "OK"),
    _400_BAD_REQUEST(400, "Bad Request"),
    _403_FORBIDDEN(403, "Forbidden"),
    _404_NOT_FOUND(404, "Not Found"),
    _405_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    _500_INTERNAL_ERROR(500, "Internal Server Error"),
    _501_NOT_IMPLEMENTED(501, "Not Implemented"),

    ;

    public final int STATUS_CODE;
    public final String MESSAGE;

    StatusEnum(int StatCode, String Message) {
        this.STATUS_CODE = StatCode;
        this.MESSAGE = Message;
    }
}
