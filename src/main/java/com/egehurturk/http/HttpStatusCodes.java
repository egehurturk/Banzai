package com.egehurturk.http;

public enum HttpStatusCodes {
    _400_BAD_REQUEST(400, "Bad Request"),
    _405_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    _200_OK(200, "OK"),
    _403_FORBIDDEN(403, "Forbidden"),
    _404_NOT_FOUND(404, "Not Found"),
    _500_INTERNAL_ERROR(500, "Internal Server Error"),
    _501_NOT_IMPLEMENTED(501, "Not Implemented"),

    ;

    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCodes(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }
}
