package com.egehurturk.util;

public enum StatusEnum {
    _100_CONTINUE(Constants.C100, Constants.CM100),
    _200_OK(Constants.C200, Constants.CM200),
    _400_BAD_REQUEST(Constants.C400, Constants.CM400),
    _403_FORBIDDEN(Constants.C403, Constants.CM403),
    _404_NOT_FOUND(Constants.C404, Constants.CM404),
    _405_METHOD_NOT_ALLOWED(Constants.C405, Constants.CM405),
    _500_INTERNAL_ERROR(Constants.C500, Constants.CM500),
    _501_NOT_IMPLEMENTED(Constants.C501, Constants.CM501),

    ;

    public final int STATUS_CODE;
    public final String MESSAGE;

    StatusEnum(int StatCode, String Message) {
        this.STATUS_CODE = StatCode;
        this.MESSAGE = Message;
    }

    private static class Constants {
        public static final int C100 = 100;
        public static final int C200 = 200;
        public static final int C400 = 400;
        public static final int C403 = 403;
        public static final int C404 = 404;
        public static final int C405 = 405;
        public static final int C500 = 500;
        public static final int C501 = 501;


        public static final String CM100 = "Continue";
        public static final String CM200 = "OK";
        public static final String CM400 = "Bad Request";
        public static final String CM403 = "Forbidden";
        public static final String CM404 = "Not Found";
        public static final String CM405 = "Method Not Allowed";
        public static final String CM500 = "Internal Server Error";
        public static final String CM501 = "Not Implemented";



    }
}
