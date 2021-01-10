package com.egehurturk.util;

public enum HeaderEnum {

    CONNECTION("Connection: ", HeaderStatusEnum.General),
    ACCEPT("ACCEPT", HeaderStatusEnum.Request),
    ACCEPT_ENCODING("Accept-Encoding: ", HeaderStatusEnum.Request),
    ACCEPT_LANGUAGE("Accept-Language: ", HeaderStatusEnum.Request),
    HOST("Host", HeaderStatusEnum.Request),
    SERVER("Server: ", HeaderStatusEnum.Response),
    USER_AGENT("User-Agent: ", HeaderStatusEnum.Request),
    DATE("Date: ", HeaderStatusEnum.Response),
    CONTENT_TYPE("Content-Type: ", HeaderStatusEnum.General),
    CONTENT_LENGTH("Content-Length: ", HeaderStatusEnum.General),
    CONTENT_ENCODING("Content-Encoding: ", HeaderStatusEnum.General),
    CONTENT_LANGUAGE("Content-Language: ", HeaderStatusEnum.General)
    ;

    public final String NAME;
    public final HeaderStatusEnum PLACE;

    HeaderEnum(String NAME, HeaderStatusEnum PLACE) {
        this.NAME = NAME;
        this.PLACE = PLACE;
    }
}
