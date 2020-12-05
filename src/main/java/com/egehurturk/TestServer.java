package com.egehurturk;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestServer extends BaseServer {
    public static int port = 9090;
    public TestServer(int serverPort) throws UnknownHostException {
        super(serverPort);
    }

    public static void main(String[] args) throws IOException {

        try  {
            TestServer listener = new TestServer(port);
            listener.start();
            listener.stop();
        }
        catch (UnknownHostException e){
            e.printStackTrace();
        }
    }


    @Override
    public void start() throws IOException {

        while (true) {
            Socket s = this.server.accept();
            ConnectionManager c = new ConnectionManager(s);
            c.run();
        }
    }

    @Override
    public void stop() throws IOException {
       this.server.close();
    }
}
