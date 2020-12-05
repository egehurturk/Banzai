package com.egehurturk;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        TestOneServer server1 = new TestOneServer(9090);
       // server1.start(); // WAITS HERE ????????? CUZ THIS WAITS FOREVER IN WHILE TRUE LOOP, SO CLIENT DOES EVEN NOT CONNECT!

    }
}
