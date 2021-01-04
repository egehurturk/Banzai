// A DUMMY TEST SERVER CLASS FOR TESTING PURPOSES
// BASIC TCP [X]

package com.egehurturk;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer extends BaseServer {

    protected static Logger logger = LogManager.getLogger(TCPServer.class);

    public TCPServer(int serverPort) throws UnknownHostException {
        super(serverPort);
    }

    public static void main(String[] args) throws IOException {

        ExecutorService pool = Executors.newFixedThreadPool(500);
        logger.info("Server started on port " + 9090);
        ServerSocket sv = new ServerSocket(9090, 50, InetAddress.getByName("0.0.0.0"));
        while (true) {
            Socket cli = sv.accept();
            ConnectionManager manager = new ConnectionManager(cli);
            logger.info("Connection established with " + cli + "");
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

    @Override
    public void restart() {

    }

    @Override
    public void reload() {

    }
}
