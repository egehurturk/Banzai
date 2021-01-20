package com.egehurturk.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final String SERVERIP = "192.168.8.142";
    private final int SERVERPORT = 9090;
    private Socket cli;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader keyboard;

    public void startConn() {
        try {
            cli = new Socket(SERVERIP, SERVERPORT);
            out = new PrintWriter(cli.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(cli.getInputStream()));
            keyboard = new BufferedReader(new InputStreamReader(System.in));

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startMessaging() {
        String resp = "NOT FOUND";
        try {
            while (true) {
                System.out.print("> ");
                String command = keyboard.readLine();

                if (command.equals("quit")) break;

                out.println(command);

                String serverResponse = in.readLine();

                System.out.println("Server says: " + serverResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            cli.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
