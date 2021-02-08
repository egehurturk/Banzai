package com.egehurturk;


import com.egehurturk.exceptions.HttpRequestException;
import com.egehurturk.httpd.HttpRequest;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Http Request tests (%80 coverage)")
public class HttpOnlyGetRequestTest {


    public HttpRequest testRequest;
    public static String validTestGetRequest = "GET / HTTP/1.1\r\n" +
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
    public static String invalidTestRequestWithInvalidMethod = "UPLOAD / HTTP/1.1\r\n" +
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
    public static String invalidTestRequestWithInvalidHttp = "GET / HTTP/2.0\r\n" +
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

    /**
     * --------------------------------- VALID HTTP REQUEST TESTING ------------------------------
     */

    @ParameterizedTest
    @MethodSource("httpProviderValidTestGetRequest")
    @DisplayName("Test valid http request's method to be GET")
    public void testHttpRequestMethod(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertEquals(testRequest.getMethod(), "GET");
    }

    @ParameterizedTest
    @MethodSource("httpProviderValidTestGetRequest")
    @DisplayName("Test valid http request's scheme to be HTTP/1.1")
    public void testHttpRequestScheme(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertEquals(testRequest.getScheme(), "HTTP/1.1");
    }

    @ParameterizedTest
    @MethodSource("httpProviderValidTestGetRequest")
    @DisplayName("Test valid http request's path to be /")
    public void testHttpRequestPath(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertEquals(testRequest.getPath(), "/");
    }

    @ParameterizedTest
    @MethodSource("httpProviderValidTestGetRequest")
    @DisplayName("Test valid http request's header host to be localhost:8080")
    public void testHttpRequestHeaderHost(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertEquals(testRequest.headers.get("host").trim(), "localhost:8080");
    }

    @ParameterizedTest
    @MethodSource("httpProviderValidTestGetRequest")
    @DisplayName("Test valid http request's header connection to be keep-alive")
    public void testHttpRequestHeaderConnection(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertEquals(testRequest.headers.get("connection").trim(), "keep-alive");
    }

    @ParameterizedTest
    @MethodSource("httpProviderValidTestGetRequest")
    @DisplayName("Test valid http request's header user-agent to be mozilla")
    public void testHttpRequestHeaderUserAgent(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertEquals(testRequest.headers.get("user-agent").trim(), "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36".toLowerCase());
    }


    @ParameterizedTest
    @MethodSource("httpProviderValidTestGetRequest")
    @DisplayName("Test valid http request's header body to be null (b/c it is GET)")
    public void testHttpRequestBodyToBeNullInGetRequest(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertNull(testRequest.getBody());
    }

    /*
        ------------------ END OF TEST VALID HTTP -----------------------
     */

    /*
        ------------------ TEST INVALID GET REQUEST
     */
    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidMethod")
    @DisplayName("Test invalid http request's invalid method that asserts BadRequest400")
    public void testInvalidHttpRequestMethod(String http) throws IOException, HttpRequestException {
        testRequest = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))));
        Assert.assertEquals("UPLOAD", testRequest.getMethod());
    }


    /*
        ------------------- END OF INVALID HTTP REQUEST GET -----------------
     */

    /*
        ------------------- TEST INVALID HTTP REQUEST SCHEME -----------------
     */

    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidHttp")
    @DisplayName("Test invalid http request's valid method (invalid scheme) that asserts BadRequest400")
    public void testInvalidHttpSchemeRequestMethod(String http) throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))))
        );
    }

    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidHttp")
    @DisplayName("Test invalid http request's valid scheme that asserts BadRequest400")
    public void testInvalidHttpSchemeRequestScheme(String http) throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))))
        );
    }

    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidHttp")
    @DisplayName("Test invalid http request's valid path (invalid scheme) that asserts BadRequest400")
    public void testInvalidHttpSchemeRequestPath(String http) throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))))
        );
    }

    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidHttp")
    @DisplayName("Test invalid http request's valid header-host (invalid scheme) that asserts BadRequest400")
    public void testInvalidHttpSchemeRequestHeaderHost(String http) throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))))
        );
    }

    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidHttp")
    @DisplayName("Test invalid http request's hedaer connection (invalid scheme) that asserts BadRequest400")
    public void testInvalidHttpSchemeRequestHeaderConnection(String http) throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))))
        );
    }

    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidHttp")
    @DisplayName("Test invalid http request's header user-agent (invalid scheme) that asserts BadRequest400")
    public void testInvalidHttpSchemeRequestHeaderUserAgent(String http) throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))))
        );
    }


    @ParameterizedTest
    @MethodSource("httpProviderInvalidTestRequestWithInvalidHttp")
    @DisplayName("Test invalid http request's header body (invalid scheme) that asserts BadRequest400")
    public void testInvalidHttpSchemeRequestBodyToBeNullInGetRequest(String http) throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(http))))
        );
    }

    @Test
    @DisplayName("Test null buffered stream for client that is null (asserts BadRequest)")
    public void testNullBufferedStreamException() throws IOException {
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(null)
        );
    }

    @Test
    @DisplayName("Test null request for client (asserts BadRequest)")
    public void testNullHttpRequest() {
        String nullHttp = " ";
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader( new InputStreamReader(prepareIncomingRequestStream(nullHttp))))
        );
    }

    @Test
    @DisplayName("Test invalid header for client (asserts BadRequest)")
    public void testInvalidHeader() {
        String invalidHttp =  "GET / HTTP/1.1\r\n" +
                "This is invalid\r\n" +
                "\r\n";
        assertThrows(com.egehurturk.exceptions.HttpRequestException.class,
                () -> new HttpRequest(new BufferedReader( new InputStreamReader(prepareIncomingRequestStream(invalidHttp))))
        );
    }


    public InputStream prepareIncomingRequestStream(String stream) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(stream.getBytes());
        return inputStream;
    }


    private static Stream<Arguments> httpProviderValidTestGetRequest() {
        return Stream.of(
                Arguments.of(validTestGetRequest)
        );
    }

    private static Stream<Arguments> httpProviderInvalidTestRequestWithInvalidMethod() {
        return Stream.of(
                Arguments.of(invalidTestRequestWithInvalidMethod)
        );
    }

    private static Stream<Arguments> httpProviderInvalidTestRequestWithInvalidHttp() {
        return Stream.of(
                Arguments.of(invalidTestRequestWithInvalidHttp)
        );
    }
}
