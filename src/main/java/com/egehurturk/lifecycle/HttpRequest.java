package com.egehurturk.lifecycle;

import com.egehurturk.BaseServer;
import com.egehurturk.HttpServer;
import com.egehurturk.HttpValues;
import com.egehurturk.exceptions.HttpRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This server uses {@link HttpRequest} and {@link } to
 * pass state through server
 * The server, specifically {@link HttpServer} for HTTP Based communication
 * will get in this object as META information, e.g. headers
 *
 *
 * <p> Using this class is convenient to store Metadata about
 * HTTP request lifecycle, reaching headers in a dictionary
 * format. Every impending request will be wrapped around this.
 *
 * <p> Fields provide basic container-like acccess to requests.
 *
 * Client, e.g. Chrome (browser) will make the request. {@link java.net.ServerSocket#accept()} method
 * accepts the request, as defined in {@link BaseServer}.
 *
 * Note that the values (fields) of this matches with the "Accept-<...>" values in {@link HttpValues}
 *
 * ~ more description needed ~
 *
 * @author      Ege Hurturk
 * @version     1.0 - SNAPSHOT
 */
public class HttpRequest {
    /**
     * A string representing the scheme of the connection.
     * <i>"http"</i> or <i>"https"</i> only.
     */
    public String scheme;

    /**
     * ======= ONLY IN POST REQUEST =========
     *
     * The raw HTTP request body as a byte array.
     * It is convenient to use or store contents of a file with
     * a {@link java.io.InputStream} for memory-related problems.
     *
     * Body is not seen in <b>GET</b> requests. However,
     * <b>POST</b> requests do use body to post data into
     * server.
     */
    public byte[] body;

    /**
     * A string representing the full path to the requested page,
     * not including the scheme or domain.
     *
     * Requested pages (in GET request) will be avaliable through
     * here, unless specified. Converting this String to {@link java.nio.file.Path}
     * might be neccessary while processing HTTP requests in {@link HttpServer}.
     *
     * Example:
     *  "/music/guitarists/jimi_hendrix/"
     */
    public String path;

    /**
     * A string representing the HTTP method used in the request.
     * This is guaranteed to be uppercase.
     * For example:
     * "GET", "POST", "HEAD", "OPTIONS", "PUT", "PATCH", "DELETE", etc.
     *
     * Sample methods include:
     * <ul>
     *     <li> GET </li>
     *     <li> HEAD </li>
     *     <li> POST </li>
     *     <li> PUT </li>
     *     <li> DELETE </li>
     *     <li> PATCH </li>
     * </ul>
     *
     */
    public String method;

    /**
     * Other headers that are not specified as a field
     * in this object.
     */
    public HashMap<String, String> headers;

    public HttpRequest(BufferedReader data) {
        try {
            parse(data);
        }
        catch (IOException err){
            err.printStackTrace();
            // TODO: Logging
        }
        catch (HttpRequestException err) {
            err.printStackTrace();
            // TODO: Logging
        }

    }

    private void parse(BufferedReader in) throws IOException, HttpRequestException {
        // Parse the request line. This line contains 3 vital things:
        // 1. Method
        // 2. URI
        // 3. HTTP Version or Scheme
        // GET /index.html HTTP/1.1
        if (in.readLine() == null || in.readLine().isEmpty()) {
            throw new com.egehurturk.exceptions.HttpRequestException("Input stream is null or empty. Check for client" +
                    "connection that sends the request");
            // TODO: Logging
        }
        String header = in.readLine();

        // construct a tokenizer from the first line, with a delimiter of " "
        StringTokenizer tokenizer = new StringTokenizer(header);
        this.method = tokenizer.nextToken().toUpperCase(); // ensure it is all upper ("GET")
        this.path = tokenizer.nextToken().toLowerCase(); // ensure it is all lower, i.e ("/index.html")
        this.scheme = tokenizer.nextToken(); // by default it is all upper. Case here does not matter

        // read headers line by line
        String headerLine;
        while ( in.readLine() != null && !in.readLine().isEmpty()) { // check if lines are not empty
            // <key> : <value>
            // trim: <key>:<value>
            // connection:keep-alive, accept:text/html
            headerLine = in.readLine().toLowerCase().trim();
            int idx = headerLine.indexOf(":"); // get the index of ":"
            if (idx == -1) {
                throw new com.egehurturk.exceptions.HttpRequestException("Invalid header paramter: " + headerLine);
            }
            else {
                // put the header inside the headers map as
                // content-type: text/html
                this.headers.put(headerLine.substring(0, idx), headerLine.substring(idx+1, headerLine.length()));
            }

        }
        // in POST requests
        String bodyMsg;
        StringBuffer _bodyTemplate = new StringBuffer();
        while ( (bodyMsg = in.readLine()) != null ) {
            _bodyTemplate.append(bodyMsg).append("\r\n"); // append carriage return
        }
        this.body = _bodyTemplate.toString().getBytes();
    }

}

/* https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages */
/* TODO: Think about the methods for this class */
/* TODO: Create a method to enclose every field in a map. Call this headers. Remove META Map and make every header a field*/
