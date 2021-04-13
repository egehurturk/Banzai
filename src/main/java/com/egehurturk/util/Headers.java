package com.egehurturk.util;

public enum Headers {

    CONNECTION(Constants.CCONNECTION, HeaderStatus.General),
    ACCEPT(Constants.CACCEPT, HeaderStatus.Request),
    ACCEPT_ENCODING(Constants.CACCEPT_ENCODING, HeaderStatus.Request),
    ACCEPT_LANGUAGE(Constants.CACCEPT_LANGUAGE, HeaderStatus.Request),
    HOST(Constants.CHOST, HeaderStatus.Request),
    SERVER(Constants.CSERVER, HeaderStatus.Response),
    USER_AGENT(Constants.CUSER_AGENT, HeaderStatus.Request),
    DATE(Constants.CDATE, HeaderStatus.Response),
    CONTENT_TYPE(Constants.CCONTENT_TYPE, HeaderStatus.General),
    CONTENT_LENGTH(Constants.CCONTENT_LENGTH, HeaderStatus.General),
    CONTENT_ENCODING(Constants.CCONTENT_ENCODING, HeaderStatus.General),
    CONTENT_LANGUAGE(Constants.CCONTENT_LANGUAGE, HeaderStatus.General)
    ;

    public final String NAME;
    public final HeaderStatus PLACE;

    Headers(String NAME, HeaderStatus PLACE) {
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


