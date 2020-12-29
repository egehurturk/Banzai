package com.egehurturk.exceptions;

public class FileSizeOverflowException extends Exception {
    public FileSizeOverflowException(String errMsg) {
        super(errMsg);
    }

    public FileSizeOverflowException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }
}
