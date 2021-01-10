package com.egehurturk.example;

import java.io.IOException;

public class TCPServerDriver {
    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer();
        tcpServer.setConfigPropFile("src/main/resources/server.properties");
        System.out.println(tcpServer.getConfigPropFile());
        tcpServer.configureServer();
        tcpServer.start();
    }
}
