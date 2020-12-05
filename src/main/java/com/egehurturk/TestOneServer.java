// A DUMMY TEST SERVER CLASS FOR TESTING PURPOSES
// BASIC TCP [X]

package com.egehurturk;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestOneServer extends BaseServer {

    public TestOneServer(int serverPort) throws UnknownHostException {
        super(serverPort);
    }

    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(500);
        System.out.println("[SERVER STARED] on port " + 9090);
        ServerSocket sv = new ServerSocket(9090);
        while (true) {
            ConnectionManager manager = new ConnectionManager(sv.accept());
            System.out.println("[SERVER]");
            pool.execute(manager);
        }
    }


    @Override
    public void start() throws IOException {
//        ExecutorService pool = Executors.newFixedThreadPool(500);
//        System.out.println("[SERVER STARED] on port " + this.serverPort + " and on host " + this.serverHost);
//        ServerSocket sv = new ServerSocket(this.serverPort);
//        while (true) {
//            ConnectionManager manager = new ConnectionManager(sv.accept());
//            pool.execute(manager);
//        }
        System.out.println("Hello, world!");
    }

    @Override
    public void stop() throws IOException {
        this.server.close();

    }
}
