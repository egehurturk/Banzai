package com.egehurturk;


import java.io.IOException;

public class DriverClassForTest {
    public static void main(String[] args) throws IOException {
        HttpServer tcpServer = new HttpServer();
        tcpServer.setConfigPropFile("src/main/resources/server.properties");
        tcpServer.configureServer();
        System.out.println("[DEBUG][DEBUG] starting server from driver [DriverClassForTest/main]");
        tcpServer.start();
    }
}
