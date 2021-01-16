package com.egehurturk;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.StringTokenizer;

@RunWith(MockitoJUnitRunner.class)
public class HttpServerTest {

    @Mock
    private Socket client;

    private ByteArrayOutputStream outputStream;

    private HttpServer serverTest;
    private HttpServer.HttpManager manager;

    @Before
    public void setUp() throws IOException {
        outputStream = new ByteArrayOutputStream();
        Mockito.when(client.getOutputStream()).thenReturn(outputStream);
        Mockito.when(client.getInetAddress()).thenReturn(InetAddress.getByName("localhost"));
        Properties prop = new Properties();
        prop.put("server.webroot", "www");
        serverTest = new HttpServer();
        manager = serverTest.new HttpManager(client, prop);
    }

    @Test
    public void request_index_success() throws IOException {
        prepareIncomingRequestStream(generateIncomingRequest("GET", "/"));

        manager.run();

        StringTokenizer token = new StringTokenizer(new String(outputStream.toByteArray()));
        Assertions.assertEquals("HTTP/1.1", token.nextToken());
        Assertions.assertEquals("200", token.nextToken());
    }

    private String generateIncomingRequest(String method, String resource) {
        return method + " " + resource + " HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "User-Agent: curl/7.61.1\n" +
                "Accept: */*\n";
    }

    private void prepareIncomingRequestStream(String stream) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(stream.getBytes());
        Mockito.when(client.getInputStream()).thenReturn(inputStream);
    }

}
