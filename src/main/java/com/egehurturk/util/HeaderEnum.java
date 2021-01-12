package com.egehurturk.util;

public enum HeaderEnum {

    CONNECTION(Constants.CCONNECTION, HeaderStatusEnum.General),
    ACCEPT(Constants.CACCEPT, HeaderStatusEnum.Request),
    ACCEPT_ENCODING(Constants.CACCEPT_ENCODING, HeaderStatusEnum.Request),
    ACCEPT_LANGUAGE(Constants.CACCEPT_LANGUAGE, HeaderStatusEnum.Request),
    HOST(Constants.CHOST, HeaderStatusEnum.Request),
    SERVER(Constants.CSERVER, HeaderStatusEnum.Response),
    USER_AGENT(Constants.CUSER_AGENT, HeaderStatusEnum.Request),
    DATE(Constants.CDATE, HeaderStatusEnum.Response),
    CONTENT_TYPE(Constants.CCONTENT_TYPE, HeaderStatusEnum.General),
    CONTENT_LENGTH(Constants.CCONTENT_LENGTH, HeaderStatusEnum.General),
    CONTENT_ENCODING(Constants.CCONTENT_ENCODING, HeaderStatusEnum.General),
    CONTENT_LANGUAGE(Constants.CCONTENT_LANGUAGE, HeaderStatusEnum.General)
    ;

    public final String NAME;
    public final HeaderStatusEnum PLACE;

    HeaderEnum(String NAME, HeaderStatusEnum PLACE) {
        this.NAME = NAME;
        this.PLACE = PLACE;
    }

    private static class Constants {
        public static final String CCONNECTION = "Connection: ";
        public static final String CACCEPT = "Accept: ";
        public static final String CACCEPT_LANGUAGE = "Accept-Language: ";
        public static final String CACCEPT_ENCODING = "Accept-Encoding: ";
        public static final String CHOST = "Host: ";
        public static final String CSERVER = "Server: ";
        public static final String CUSER_AGENT = "User-Agent: ";
        public static final String CDATE = "Date: ";
        public static final String CCONTENT_TYPE = "Content-Type: ";
        public static final String CCONTENT_LENGTH = "Content-Length: ";
        public static final String CCONTENT_ENCODING = "Content-Encoding: ";
        public static final String CCONTENT_LANGUAGE = "Content-Language: ";
    }
}
