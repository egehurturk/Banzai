package com.egehurturk.util;

public enum MethodEnum {
    GET("GET"),
    POST("POST");

    public final String str;

    MethodEnum(String str) {
        this.str = str;
    }
}
