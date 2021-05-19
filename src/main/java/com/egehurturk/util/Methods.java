package com.egehurturk.util;

public enum Methods {

    GET(Constants.CGET),
    POST(Constants.CPOST),
    HEAD(Constants.CHEAD),
    PUT(Constants.CPUT)

    ;

    public final String str;

    Methods(String str) {
        this.str = str;
    }

    private static class Constants {
        public static final String CGET = "GET";
        public static final String CPOST = "POST";
        public static final String CHEAD = "HEAD";
        public static final String CPUT = "PUT";

    }
}
