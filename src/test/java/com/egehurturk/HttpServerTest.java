package com.egehurturk;

import com.egehurturk.handlers.HandlerTemplate;
import com.egehurturk.handlers.HttpController;
import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.util.Methods;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


@ExtendWith(MockitoExtension.class)
public class HttpServerTest {

    @Mock
    private Socket client;

    private ByteArrayOutputStream outputStream;

    public HttpController manager;
    public HttpHandler handler;

    @BeforeEach
    public void setUp() throws IOException {
        outputStream = new ByteArrayOutputStream();
        Mockito.lenient().when(client.getOutputStream()).thenReturn(outputStream);
        Mockito.lenient().when(client.getInetAddress()).thenReturn(InetAddress.getByName("localhost"));
        Properties props = new Properties();
        props.put("server.name", "Banzai");
        props.put("server.webroot", "www");
        handler = new HttpHandler(props);
        List<HandlerTemplate> handlers = new ArrayList<>();
        HandlerTemplate temp = new HandlerTemplate(Methods.GET, "/*", handler);
        handlers.add(temp);
        manager = new HttpController(client, handlers);
    }

    @AfterEach
    public void tearDown() throws IOException {
        outputStream.close();
        manager.close();
        client.close();
    }


    @Test
    @DisplayName("GET request to / path should return HTTP/1.1 200 OK")
    public void testGetRequestResponseStatusLinePathSlashDirectory() throws IOException {
        // setup the request
        prepareIncomingRequestStream(generateIncomingGetRequest("GET", "/"));
        Assertions.assertNotNull(manager);
        System.out.println(manager);
        // test
        manager.run();

        // verify
        StringTokenizer tokenizer = new StringTokenizer(new String(outputStream.toByteArray()));
        Assertions.assertEquals("HTTP/1.1", tokenizer.nextToken());
        Assertions.assertEquals("200", tokenizer.nextToken());
    }

    @Test
    @DisplayName("GET request to /index.html path should return HTTP/1.1 200 OK")
    public void testGetRequestResponseStatusLinePathIndexHtml() throws IOException {
        // setup the request
        prepareIncomingRequestStream(generateIncomingGetRequest("GET", "/index.html"));
        Assertions.assertNotNull(manager);
        // test
        manager.run();

        // verify
        StringTokenizer tokenizer = new StringTokenizer(new String(outputStream.toByteArray()));
        Assertions.assertEquals("HTTP/1.1", tokenizer.nextToken());
        Assertions.assertEquals("200", tokenizer.nextToken());
    }


    @Test
    @DisplayName("GET request to subdirectory (/css/main.css) should return HTTP/1.1 200 OK")
    public void requestSubDirectoryFound() throws IOException {
        prepareIncomingRequestStream(generateIncomingGetRequest("GET", "/css/main.css"));
        Assertions.assertNotNull(manager);
        manager.run();

        String[] splitted = new String(outputStream.toByteArray()).split(" ");
        Assertions.assertEquals("HTTP/1.1", splitted[0]);
        Assertions.assertEquals("200", splitted[1]);
    }

    @Test
    @DisplayName("GET request to /facivon.ico should return HTTP/1.1 404 Not Found")
    public void requestOtherSourceNotFound() throws IOException {
        prepareIncomingRequestStream(generateIncomingGetRequest("GET", "/favicon.ico"));
        Assertions.assertNotNull(manager);
        manager.run();

        String[] splitted = new String(outputStream.toByteArray()).split(" ");
        Assertions.assertEquals("HTTP/1.1", splitted[0]);
        Assertions.assertEquals("404", splitted[1]);

    }

    @Test
    @DisplayName("GET request to higher path (../css/main.css) should return HTTP/1.1 400 Bad Request")
    public void requestRelativePathBadRequest() throws IOException {
        prepareIncomingRequestStream(generateIncomingGetRequest("GET", "../css/main.css"));
        Assertions.assertNotNull(manager);
        manager.run();

        String[] splitted = new String(outputStream.toByteArray()).split(" ");
        Assertions.assertEquals("HTTP/1.1", splitted[0]);
        Assertions.assertEquals("400", splitted[1]);


    }


    private String generateIncomingGetRequest(String method, String path) {
        return method + " " +  path + " HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: en-US,en;q=0.9,es;q=0.8,pt;q=0.7,de-DE;q=0.6,de;q=0.5,la;q=0.4\r\n" +
                "\r\n";
    }
    private void prepareIncomingRequestStream(String stream) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(stream.getBytes());
        Mockito.lenient().when(client.getInputStream()).thenReturn(inputStream);
    }

}
