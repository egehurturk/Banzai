package com.egehurturk.httpd;


import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.util.MethodEnum;

import java.io.IOException;

public class DriverClassForTest {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.setConfigPropFile("src/main/resources/server.properties");
        httpServer.configureServer();
        HttpHandler handler = new HttpHandler(httpServer.getConfig());
        // FIXME: DOES NOT WORK ON CUSTOM URL'S
        String path = new String("/*");
        httpServer.addHandler(MethodEnum.GET, path, handler);
        httpServer.start();
    }
}
