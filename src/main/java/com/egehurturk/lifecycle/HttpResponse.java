package com.egehurturk.lifecycle;

import com.egehurturk.HttpServer;
import com.egehurturk.HttpValues;
import com.egehurturk.exceptions.HttpResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
 * accepts the request, as defined in {@link com.egehurturk.BaseServer}.
 *
 * Note that the values (fields) of this matches with the "Accept-<...>" values in {@link com.egehurturk.HttpValues}
 *
 * ~ more description needed ~
 *
 * @author      Ege Hurturk
 * @version     1.0 - SNAPSHOT
 */
public class HttpResponse {

    /*
     * The design perspective is that the first line of both Requests and Responses
     * are the fields that the class store as separate fields. Other headers
     * are stored in a Map
     *
     * HTTP/1.1 404 Not Found
     */

    /**
     * A string representing the scheme of the connection.
     * <i>"http"</i> or <i>"https"</i> only.
     *
     * E.g: HTTP/1.1
     */
    public String scheme;

    /**
     *
     * The body message (usually in XML or HTTP) that the requested
     * file or path is
     * It is convenient to use or store contents of a file with
     * a {@link java.io.InputStream} for memory-related problems.
     *
     */
    public byte[] body;

    /**
     * An integer representing the relevant HTTP status code
     *
     * All status codes are defined in {@link com.egehurturk.HttpValues}.
     *
     */
    public int code;

    /**
     * A string message representing the relevant message for HTTP
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
    public String message;

    /**
     * Other headers that are not specified as a field
     * in this object.
     */
    public HashMap<String, String> headers;

    protected static Logger logger = LogManager.getLogger(HttpResponse.class);

    // empty constructors
    public HttpResponse(HashMap<String, String> map, String scheme, int code, String message, byte[] bod) {}
    public HttpResponse() {}


    /**
     * Creates {@link this}
     * @param map                       - Headers for <b>response</b>. Note that this map does not
     *                                  contain <u>any headers</u> for request. This map is written
     *                                  manually by the server
     * @param scheme                    - Http scheme (HTTP/1.1)
     * @param code                      - Status code
     * @param message                   - Status message
     * @param body                      - Content body
     * @return                          - new {@link HttpResponse} object
     * @throws HttpResponseException    - any errors associated with response lifecycle
     */
    public static HttpResponse create(HashMap<String, String> map, String scheme, int code, String message, String body)
            throws HttpResponseException {
        HttpResponse resp = new HttpResponse();

        Set<Integer> codes = prepareCodes(new HashSet<Integer>());
        Set<String> messages = prepareMessages(new HashSet<String>());

        if (!codes.contains(code)) {
            logger.info("Status code does not exists, or not implemented");
            throw new com.egehurturk.exceptions.HttpResponseException("Status code does not exists, or is not" +
                    "implemented by me. See future versions for more");
        }

        if (!messages.contains(message)) {
            logger.info("Message does not exists, or not implemented");
            throw new com.egehurturk.exceptions.HttpResponseException("Message does not exists, or is not" +
                    "implemented by me. See future versions for more");
        }

        resp.code = code;
        resp.message = message;
        resp.scheme = scheme;
        resp.headers = map;
        resp.body = body.getBytes();
        return resp;
    }

    public <K extends String, V extends String> void set(K key, V value) {
        this.headers.put(key, value);
    }

    // Utility functions

    private static Set<Integer> prepareCodes(Set<Integer> code) {
        code.add(HttpValues.Code.BAD_REQ);
        code.add(HttpValues.Code.FORBIDDEN);
        code.add(HttpValues.Code.INTERNAL_ERROR);
        code.add(HttpValues.Code.METHOD_NOT_FOUND);
        code.add(HttpValues.Code.NOT_FOUND);
        code.add(HttpValues.Code.NOT_IMPLEMENTED);
        code.add(HttpValues.Code.OK);

        return code;
    }

    private static Set<String> prepareMessages(Set<String> msg) {
        msg.add(HttpValues.Messages.BAD_REQ);
        msg.add(HttpValues.Messages.FORBIDDEN);
        msg.add(HttpValues.Messages.INTERNAL_ERROR);
        msg.add(HttpValues.Messages.METHOD_NOT_ALLOWED);
        msg.add(HttpValues.Messages.NOT_FOUND);
        msg.add(HttpValues.Messages.NOT_IMPLEMENTED);
        msg.add(HttpValues.Messages.OK);

        return msg;
    }


}