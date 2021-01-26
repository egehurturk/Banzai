package com.egehurturk.example;

import com.egehurturk.exceptions.ConfigurationException;

import java.io.IOException;

public class TCPServerDriver {
    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer();
        tcpServer.setConfigPropFile("src/main/resources/server.properties");
        System.out.println(tcpServer.getConfigPropFile());
        try {
            tcpServer.configureServer();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        tcpServer.start();
    }
}
