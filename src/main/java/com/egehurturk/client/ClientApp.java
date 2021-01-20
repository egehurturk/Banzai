package com.egehurturk.client;

public class ClientApp {
    public static void main(String[] args) {
        Client cli1 = new Client();
        cli1.startConn();
        cli1.startMessaging();
    }
}
