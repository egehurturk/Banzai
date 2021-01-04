package com.egehurturk.lifecycle;


import com.egehurturk.exceptions.HttpResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class HttpResponseBuilder {
    private String scheme;
    private String body;
    private int code;
    private String message;
    private HashMap<String, String> headers;
    protected static Logger logger = LogManager.getLogger(HttpResponseBuilder.class);

    private HttpResponseBuilder() {}

    public HttpResponseBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public HttpResponseBuilder body(String body) {
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

    public HttpResponse build() {
        HttpResponse res = null;
        try {
            res = HttpResponse.create(this.headers, scheme, code, message, body);
        } catch (HttpResponseException e) {
            logger.error("Error building Http Response");
            e.printStackTrace();
        }
        return res;

    }

}
