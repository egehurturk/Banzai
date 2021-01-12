package com.egehurturk;


import java.io.IOException;

public class DriverClassForTest {
    public static void main(String[] args) throws IOException {
        HttpServer tcpServer = new HttpServer();
        tcpServer.setConfigPropFile("src/main/resources/server.properties");
        System.out.println(tcpServer.getConfigPropFile());
        tcpServer.configureServer();
        tcpServer.start();
    }
}
