package com.egehurturk.httpd;

import com.egehurturk.core.BaseServer;
import com.egehurturk.util.Headers;
import com.egehurturk.util.Methods;
import com.egehurturk.util.Pair;
import com.egehurturk.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
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
    private String scheme;

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
    private byte[] body;

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
    private String path;

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
    private String method;

    private HashMap<String, String> queryParams = new HashMap<String, String>();

    // logger instance
    private static Logger logger = LogManager.getLogger(HttpRequest.class);

    /** Client stream */
    private BufferedReader data;

    public final String METHOD = "method";
    public final String PROTOCOL = "protocol";
    public final String URL_RESOURCE = "resource";
    public final String HTTP_V_1_1 = "HTTP/1.1";
    public final String HTTP_V_1_0 = "HTTP/1.0";

    private final String BAD_REQ_MSG = "Input stream is null or empty. Check for client" +
        " connection that sends the request";
    private final String HTTP_ER_MSG = "Request does not match HTTP standards. Check the request again " +
        "and/or read RCF standards. Request should contain at least method (\"GET, POST\"), " +
        "HTTP scheme (\"HTTP/1.1\"), and path";

    /**
     * Other headers that are not specified as a field
     * in this object.
     */
    public HashMap<String, String> headers = new HashMap<>();

    public HttpRequest(BufferedReader data) {
        this.data = data;
    }


    public boolean parse() throws IOException {
        if (this.data == null)
            return false;

        String requestLine = this.data.readLine();

        /*
            Some browsers open multiple socket connections to a server because of safety (e.g., Chrome opens
            an additional socket so that it's ready if the first one closes). These sockets do not send any
            text from stream; thus, representing EOS (End of Stream). So, requestline may be null when these sockets are
            connected. Thus, I throw an exception here and catch it in HttpController to handle this specific
            case. The controller just closes up the connection w/o sending anything.
         */
        if (requestLine == null)
            throw new IllegalArgumentException();


        if (requestLine.isEmpty())
            return false;

        String[] requestLineArray = requestLine.split(" ");

        boolean isHttpValid = requestLineArray.length == 3 && checkValidHttpRequest(requestLine);
        if (!isHttpValid)
            return false;

        this.method   = requestLineArray[0].toUpperCase(); // ensure it is all upper ("GET")
        if (!requestLineArray[1].contains("?")) {
            this.path = requestLineArray[1].toLowerCase(); // ensure it is all lower, i.e ("/index.html")
        } else {
            this.path = requestLineArray[1].substring(0, requestLineArray[1].indexOf("?")); // /index.html?a=3&b=4 -> /index.html
            parseQueryParams(requestLineArray[1].substring(                                 // a=3&b=4
                    requestLineArray[1].indexOf("?") + 1));
        }
        this.scheme   = requestLineArray[2];

        // read headers line by line
        String  headerLine = this.data.readLine().toLowerCase().trim();
        boolean pass = true;
        while (!headerLine.isEmpty()) {
            int idx = headerLine.indexOf(":");
            if (idx == -1) {
                pass = false;
                break;
            }
            else {
                this.headers.put(headerLine.substring(0, idx), headerLine.substring(idx+1));
            }
            headerLine = this.data.readLine().toLowerCase().trim();
        }

        if (!pass) {
            logger.warn("Invalid header parameter: " + headerLine);
            return false;
        }

        if (!this.headers.containsKey(
            Utility.removeLastChars(Headers.HOST.NAME.trim().toLowerCase(), 1))
            || this.headers.get(Utility.removeLastChars(Headers.HOST.NAME.trim().toLowerCase(), 1)).equals("")
        )
            return false;


        // POST requests have body after the metadata (headers + status line)
        if (method.equals("POST")) {
            String bodyMsg;
            StringBuilder _bodyTemplate = new StringBuilder();
            while ( (bodyMsg = this.data.readLine()) != null ) {
                _bodyTemplate.append(bodyMsg).append("\r\n"); // append carriage return
            }
            this.body = _bodyTemplate.toString().getBytes();
        }

        return true;
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
        if (requestLine.length != 3) {
            logger.error("Request line not found");
            return false;
        }
        String method = requestLine[0].toUpperCase(); // "GET"
        String scheme = requestLine[2];

        if (!(scheme.equals(HTTP_V_1_1) || scheme.equals(HTTP_V_1_0))) {
            logger.warn("HTTP Version not supported");
            return false;
        }
        try {
            Methods.valueOf(method);
        } catch (IllegalArgumentException er) {
            return false;
        }
        return true;
    }

    private void parseQueryParams(String queryStr) {
        for (String param: queryStr.split("&")) {   // ["a=3"], ["b=4"], ["c=5"]
            int separator = param.indexOf('='); // index of "="
            if (separator > -1)  {              // if there is "="
                queryParams.put(param.substring(0, separator),  // put {"a", "3"} in query parameters
                        param.substring(separator + 1));
            } else  {
                queryParams.put(param, null);       // put {"d-43", null}
            }
        }
    }


    public String getScheme() {
        return scheme;
    }

    public byte[] getBody() {
        return body;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public boolean hasHeader(String headerName) {
        return getHeaderPair(headerName).getFirst();
    }

    public String getHeader(String name) {
        if (hasHeader(name))
            return getHeaderPair(name).getSecond();
        return null;
    }

    private Pair<Boolean, String> getHeaderPair(String name) {
        String ret = this.headers.get(name);
        Pair<Boolean, String> pair;
        if (ret == null) {
            pair = Pair.makePair(false, null);
        } else {
            pair = Pair.makePair(true, ret);
        }
        return pair;
    }

    public boolean hasParameter(String param) {
        return getQueryParam(param).getFirst();
    }

    public String getParameter(String param) {
        if (hasParameter(param))
            return getQueryParam(param).getSecond();
        return null;
    }

    private Pair<Boolean, String> getQueryParam(String param) {
        Pair<Boolean, String> pair = null;
        if (queryParams.get(param) == null) {
            logger.warn("Query parameter is null for " + param + " parameter");
            pair = Pair.makePair(false, null);
        } else {
            pair =  Pair.makePair(true, queryParams.get(param));
        }
        return pair;
    }
}

/* https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages */
