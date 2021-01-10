package com.egehurturk;
import com.egehurturk.example.TCPServer;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        TCPServer server1 = new TCPServer(9090);
       // server1.start();

    }
}
