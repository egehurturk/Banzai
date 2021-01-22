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
        // FIXME: DOES NOT WORK ON CUSTOM URL'S
        String path = "/hello";
        httpServer.allowCustomUrlMapping(true);
        httpServer.addHandler(MethodEnum.GET, path, new MyHandler());
        httpServer.addHandler(MethodEnum.GET, path, handler);
        httpServer.addHandler(MethodEnum.POST, path, handler);

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
}


/*
 * FIXME: POST REQUEST IS VERY VERY SLOW?
 */

/*
TODO: If a handler is installed with /*, and also another handler with e.g. path /hello is installed, the latter one should have higher priority
 */