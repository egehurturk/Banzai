package com.egehurturk.lifecycle;

import com.egehurturk.BaseServer;
import com.egehurturk.HttpServer;
import com.egehurturk.exceptions.HttpRequestException;
import com.egehurturk.util.MethodEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

// http://web-sniffer.net/rfc/rfc2616.html#section-14.1



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
 * Note that the values (fields) of this matches with the "Accept-<...>" values
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
     *
     *  From RFC:
     *  http_URL = "http:" "//" host [ ":" port ] [ abs_path [ "?" query ]]
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

    // logger instance
    protected static Logger logger = LogManager.getLogger(HttpRequest.class);

    public final String METHOD = "method";
    public final String PROTOCOL = "protocol";
    public final String URL_RESOURCE = "resource";
    public final String HTTP_V_1_1 = "HTTP/1.1";
    public final String HTTP_V_1_0 = "HTTP/1.0";

    /**
     * Other headers that are not specified as a field
     * in this object.
     */
    public HashMap<String, String> headers = new HashMap<>();

    public HttpRequest(BufferedReader data) {
        try {
            System.out.println("[DEBUG][DEBUG] parse [constructor]" );
            parse(data);
        }
        catch (IOException err){
            err.printStackTrace();
            logger.error("[ERROR]: Could not parse client request");
        }
        catch (HttpRequestException err) {
            err.printStackTrace();
            logger.error("[ERROR] Could not parse client request");
        }

    }

    private void parse(BufferedReader in) throws IOException, HttpRequestException {
        System.out.println("[DEBUG][DEBUG] bufferedreader in [parse] -->> " + in);
        if (in == null) {
            logger.error("Input stream of client is null, or empty, Check for client connection");
            throw new com.egehurturk.exceptions.BadRequest400Exception("Input stream is null or empty. Check for client" +
                    "connection that sends the request", 400, "Bad Request");
        }
        String requestLine = in.readLine();
        System.out.println("[DEBUG][DEBUG] requestLine [parse] -->> " + requestLine);
        System.out.println("[DEBUG][DEBUG] requestLine == null [parse] -->> " + (requestLine == null));
        if (requestLine == null || requestLine.isEmpty()) {
            logger.error("Input stream of client is null, or empty, Check for client connection");
            throw new com.egehurturk.exceptions.BadRequest400Exception("Input stream is null or empty. Check for client" +
                    " connection that sends the request", 400, "Bad Request");
        }
        System.out.println("[DEBUG][DEBUG] checkValidHttpRequest [parse] -->> " + checkValidHttpRequest(requestLine));
        if (!checkValidHttpRequest(requestLine)) {
            logger.error("Request is not valid (check scheme)");
            throw new com.egehurturk.exceptions.BadRequest400Exception("Request does not match HTTP standards. Check the request again" +
                    "and/or read RCF standards. Request should contain at least method (\"GET, POST\"), " +
                    "HTTP scheme (\"HTTP/1.1\"), and path", 400, "Bad Request");
        }

        String[] requestLineArray = requestLine.split(" ");
        System.out.println("[DEBUG][DEBUG] requestLineArray [parse] -->> " + Arrays.toString(requestLineArray));
        System.out.println("[DEBUG][DEBUG] requestLineArray.length [parse] -->> " + requestLineArray.length);
        if (requestLineArray.length != 3) {
            logger.error("Request line (e.g. GET /index HTTP/1.1 is not found");
            throw new com.egehurturk.exceptions.BadRequest400Exception("Request does not match HTTP standards. Check the request again" +
                    "and/or read RCF standards. Request should contain at least method (\"GET, POST\"), " +
                    "HTTP scheme (\"HTTP/1.1\"), and path", 400, "Bad Request");
        }
        this.method = requestLineArray[0].toUpperCase(); // ensure it is all upper ("GET")
        this.path = requestLineArray[1].toLowerCase(); // ensure it is all lower, i.e ("/index.html")
        this.scheme = requestLineArray[2]; // by default it is all upper. Case here does not matter
        System.out.println("[DEBUG][DEBUG] method [parse] -->> " + method);
        System.out.println("[DEBUG][DEBUG] path [parse] -->> " + path);
        System.out.println("[DEBUG][DEBUG] scheme [parse] -->> " + scheme);

        // read headers line by line
        // TODO: check for null `in`
        String  headerLine = in.readLine().toLowerCase().trim();
        System.out.println("[DEBUG][DEBUG] headerLine [parse] -->> " + headerLine);
        System.out.println("[DEBUG][DEBUG] headerLine == null [parse] -->> " + (headerLine == null));
        System.out.println("[DEBUG][DEBUG] headerLine.isempty() [parse] -->> " + (headerLine.isEmpty()));
        while ( !headerLine.isEmpty() && headerLine != null) { // check if lines are not empty
            System.out.println("[DEBUG][DEBUG] inf [parse/while]");
            // <key> : <value>
            // trim: <key>:<value>
            // connection:keep-alive, accept:text/html
            int idx = headerLine.indexOf(":"); // get the index of ":"
            if (idx == -1) {
                throw new com.egehurturk.exceptions.BadRequest400Exception("Invalid header paramter: " + headerLine,
                        400, "Bad Request");
            }
            else {
                // put the header inside the headers map as
                // content-type: text/html
                this.headers.put(headerLine.substring(0, idx), headerLine.substring(idx+1));
            }
            headerLine = in.readLine().toLowerCase().trim();
            System.out.println("[DEBUG][DEBUG] headerLineNew [parse] -->> " + (headerLine));

        }
        // in POST requests

        if (method.equals("POST")) {
            String bodyMsg;
            StringBuilder _bodyTemplate = new StringBuilder();
            while ( (bodyMsg = in.readLine()) != null ) {
                System.out.println("CHECK 2");
                _bodyTemplate.append(bodyMsg).append("\r\n"); // append carriage return
            }
            this.body = _bodyTemplate.toString().getBytes();
        }

    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        // first lines
        map.put(METHOD, this.method);
        map.put(URL_RESOURCE, this.path);
        map.put(PROTOCOL, this.scheme);

        for (String key : this.headers.keySet()) {
            map.put(key, this.headers.getOrDefault(key, ""));
        }
        return map;
    }

    private boolean checkValidHttpRequest(String firstLine) {

        String[] requestLine = firstLine.split(" ");
        String method = requestLine[0].toUpperCase(); // "GET"
        String scheme = requestLine[2];
        System.out.println("[DEBUG][DEBUG] requestLine [checkValidHttpRequest] -->> " + Arrays.toString(requestLine));
        System.out.println("[DEBUG][DEBUG] method [checkValidHttpRequest] -->> " + method);
        System.out.println("[DEBUG][DEBUG] scheme [checkValidHttpRequest] -->> " + scheme);

        if (!(scheme.equals(HTTP_V_1_1) || scheme.equals(HTTP_V_1_0))) {
            logger.info("HTTP Version not supported");
            return false;
        }

        for (MethodEnum methodE: MethodEnum.values()) {
            System.out.println("[DEBUG][DEBUG] method.equals(methodE.str) [checkValidHttpRequest] -->> " + method.equals(methodE.str));
            if (method.equals(methodE.str)) {
                return true;
            }
        }
        return false;
    }
}

/* https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages */
