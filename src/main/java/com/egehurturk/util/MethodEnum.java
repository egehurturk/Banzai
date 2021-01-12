package com.egehurturk.util;

public enum MethodEnum {
    GET(Constants.CGET),
    POST(Constants.CPOST);

    public final String str;

    MethodEnum(String str) {
        this.str = str;
    }

    private static class Constants {
        public static final String CGET = "GET";
        public static final String CPOST = "POST";

    }
}
