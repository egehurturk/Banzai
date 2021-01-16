package com.egehurturk;


import com.egehurturk.lifecycle.HttpRequest;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class HttpOnlyGetRequestTest {


    public HttpServer testServer;
    public HttpRequest testRequest;

    @BeforeEach
    public void beforeTest() throws UnknownHostException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(generateValidTestGetRequest())));
    }

    @AfterEach
    public void afterTest() {
        testRequest = null;
    }

    @Test
    public void testHttpRequestMethod() {
        Assert.assertEquals(testRequest.method, "GET");
    }

    @Test
    public void testHttpRequestScheme() {
        Assert.assertEquals(testRequest.scheme, "HTTP/1.1");
    }

    @Test
    public void testHttpRequestPath() {
        Assert.assertEquals(testRequest.path, "/");
    }

    @Test
    public void testHttpRequestHeaderHost() {
        Assert.assertEquals(testRequest.headers.get("host").trim(), "localhost:8080");
    }

    @Test
    public void testHttpRequestHeaderConnection() {
        Assert.assertEquals(testRequest.headers.get("connection").trim(), "keep-alive");
    }

    @Test
    public void testHttpRequestHeaderUserAgent() {
        Assert.assertEquals(testRequest.headers.get("user-agent").trim(), "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36".toLowerCase());
    }


    @Test
    public void testHttpRequestBodyToBeNullInGetRequest() {
        Assert.assertNull(testRequest.body);
    }

    /**
     * Generates a test case for testing HTTP response
     * @return
     */
    public InputStream generateValidTestGetRequest() {
        String rawData = "GET / HTTP/1.1\r\n" +
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

        InputStream inputStream = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.US_ASCII
                )
        );

        return inputStream;
    }
}
