package com.egehurturk.httpd;


import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.MethodEnum;

import java.io.IOException;
import java.io.PrintWriter;

public class DriverClassForTest {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.setConfigPropFile("src/main/resources/server.properties");
        httpServer.configureServer();
        HttpHandler handler = new HttpHandler(httpServer.getConfig());
        httpServer.allowCustomUrlMapping(true);
        httpServer.addHandler(MethodEnum.GET, "/*", handler);
        httpServer.addHandler(MethodEnum.GET, "/hello", new MyHandler());
        httpServer.addHandler(MethodEnum.POST, "/*", handler);
        httpServer.addHandler(MethodEnum.GET, "/thismynewserver", new MyNewHandler());
        httpServer.addHandler(MethodEnum.GET, "/cemhurturk", new MyHandler());

        httpServer.start();
    }

    static class MyHandler implements Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(200)
                    .message("OK")
                    .body("<h1>Hello</h1>".getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+("<h1>Hello</h1>".length()))
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }

    static class MyNewHandler implements Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(404)
                    .message("OK")
                    .body("<h1>404 Error</h1>".getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+("<h1>404 Error</h1>".length()))
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }
}


/*
 * FIXME: POST REQUEST IS VERY VERY SLOW?
 */
