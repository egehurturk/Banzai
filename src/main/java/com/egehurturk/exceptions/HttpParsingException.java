package com.egehurturk.exceptions;

import com.egehurturk.http.HttpStatusCodes;

public class HttpParsingException extends Exception {
    private HttpStatusCodes errorCode;

    public HttpParsingException(HttpStatusCodes code) {
        super(code.MESSAGE);
        this.errorCode = code;
    }

    public HttpStatusCodes getErrorCode() {
        return errorCode;
    }
}
