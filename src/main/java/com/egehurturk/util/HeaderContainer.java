package com.egehurturk.util;

/**
 * Stores identified headers
 */
public class HeaderContainer {
    public final String connection = "Connection: ";
    public final String accept = "Accept: ";
    public final String contentType = "Content-Type: "; // [X]
    public final String acceptEncoding = "Accept-Encoding: ";
    public final String contentEncoding = "Content-Encoding: ";
    public final String acceptLanguage = "Accept-Language: ";   // [X]
    public final String contentLanguage = "Content-Language: "; //[X]
    public final String server = "Server: "; // [X]
    public final String date = "Date: "; // [X]
    public final String contentLength = "Content-Length: "; // [X]
    public final String host = "Host: "; // [X]
}
