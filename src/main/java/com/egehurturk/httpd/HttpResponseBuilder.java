package com.egehurturk.httpd;


import com.egehurturk.exceptions.HttpResponseException;
import com.egehurturk.util.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.util.HashMap;

public class HttpResponseBuilder {
    private String scheme;
    private byte[] body;
    private int code;
    private String message;
    private PrintWriter stream;
    private HashMap<String, String> headers = new HashMap<String, String>();
    protected static Logger logger = LogManager.getLogger(HttpResponseBuilder.class);

    public HttpResponseBuilder() {}

    public HttpResponseBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public HttpResponseBuilder body(byte[] body) {
        this.body = body;
        return this;
    }

    public HttpResponseBuilder code(int code) {
        this.code = code;
        return this;
    }

    public HttpResponseBuilder message(String message) {
        this.message = message;
        return this;
    }

    public HttpResponseBuilder setHeader(String header, String value) {
        this.headers.put(header, value);
        return this;
    }

    public HttpResponseBuilder setStream(PrintWriter stream) {
        this.stream = stream;
        return this;
    }


    public HttpResponse build() {
        HttpResponse res = null;
        try {
            res = HttpResponse.create(this.headers, scheme, code, message, body, stream);
        } catch (HttpResponseException e) {
            logger.error("Error building Http Response");
            e.printStackTrace();
        }
        return res;
    }

    public HttpResponse factory(String scheme, int statusCode, String statusMessage, byte[] body, PrintWriter stream, String mime, String date,
                                String name, String contentLanguage, int contentLength
                                ) {
        HttpResponse response = new HttpResponseBuilder()
                .scheme(scheme)
                .code(statusCode)
                .message(statusMessage)
                .body(body)
                .setStream(stream)
                .setHeader(Headers.CONTENT_TYPE.NAME, mime)
                .setHeader(Headers.DATE.NAME, date)
                .setHeader(Headers.SERVER.NAME, name)
                .setHeader(Headers.CONTENT_LANGUAGE.NAME, contentLanguage)
                .setHeader(Headers.CONTENT_LENGTH.NAME, ""+contentLength)
                .build();
        return response;
    }

}

