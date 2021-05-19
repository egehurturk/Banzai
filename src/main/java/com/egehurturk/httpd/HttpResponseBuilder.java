package com.egehurturk.httpd;


import com.egehurturk.exceptions.HttpResponseException;
import com.egehurturk.util.Compressor;
import com.egehurturk.util.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

public class HttpResponseBuilder {
    private String scheme;
    private byte[] body;
    private int code;
    private String message;
    private PrintStream stream;
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

    public HttpResponseBuilder setStream(PrintStream stream) {
        this.stream = stream;
        return this;
    }


    public HttpResponse build() {
        HttpResponse res = null;
        try {
            res = HttpResponse.create(this.headers, scheme, code, message, body, stream);
        } catch (HttpResponseException e) {
            logger.error("Error building Http Response: ");
            e.printStackTrace();
        }
        return res;
    }

    public HttpResponse factory(String scheme, int statusCode, String statusMessage, byte[] body, PrintStream stream, String mime, String date,
                                String name, String contentLanguage, int contentLength, boolean compressEnabled
                                ) {

        if (compressEnabled) {
            byte[] compressed_gzip_body = null;
            try {
                compressed_gzip_body = Compressor.compress_GZIP(body);
                return getHttpResponse(scheme, statusCode, statusMessage, compressed_gzip_body, stream, mime, date, name, contentLanguage, compressed_gzip_body.length, "gzip");
            } catch (IOException e) {
                logger.error("Something is wrong, just don't panic: ");
                System.out.println(e.getMessage());
                return getHttpResponse(scheme, statusCode, statusMessage, body, stream, mime, date, name, contentLanguage, contentLength, null);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
            }
            return null;
        } else {
            return getHttpResponse(scheme, statusCode, statusMessage, body, stream, mime, date, name, contentLanguage, contentLength, null);
        }

    }

    private HttpResponse getHttpResponse(String scheme, int statusCode, String statusMessage, byte[] body, PrintStream stream, String mime, String date, String name, String contentLanguage, int contentLength, String contentEncoding) {
        HttpResponseBuilder builder = new HttpResponseBuilder()
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
            .setHeader(Headers.CONTENT_ENCODING.NAME, contentEncoding);
        return builder.build();
    }

}

