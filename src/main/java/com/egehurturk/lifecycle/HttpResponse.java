package com.egehurturk.lifecycle;

import com.egehurturk.HttpServer;
import com.egehurturk.exceptions.HttpResponseException;
import com.egehurturk.exceptions.NotImplemented501Exception;
import com.egehurturk.util.StatusEnum;
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
 * Note that the values (fields) of this matches with the "Accept-<...>" values
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
     */
    public int code;

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
    public static HttpResponse create(HashMap<String, String> map, String scheme, int code, String message, byte[] body)
            throws HttpResponseException  {
        System.out.println("[DEBUG][DEBUG] inside HttpResponse create function [HttpResponse/create]");
        HttpResponse resp = new HttpResponse();

        Set<Integer> codes = prepareCodes(new HashSet<Integer>());
        Set<String> messages = prepareMessages(new HashSet<String>());
        System.out.println("[DEBUG][DEBUG] check code [HttpResponse/create] -->> " + codes.contains(code));
        if (!codes.contains(code)) {
            logger.info("Status code does not exists, or not implemented");
            throw new NotImplemented501Exception("Status code does not exists, or is not" +
                    "implemented by me. See future versions for more", 501, "Not Implemented");
        }
        System.out.println("[DEBUG][DEBUG] check message [HttpResponse/create] -->> " + codes.contains(message));
        if (!messages.contains(message)) {
            logger.info("Message does not exists, or not implemented");
            throw new com.egehurturk.exceptions.NotImplemented501Exception("Message does not exists, or is not" +
                    "implemented by me. See future versions for more", 501, "Not Implemented");
        }
        System.out.println("[DEBUG][DEBUG] setting fields for response [HttpResponse/create]");
        resp.code = code;
        resp.message = message;
        resp.scheme = scheme;
        resp.headers = map;
        resp.body = body;
        System.out.println("[DEBUG][DEBUG] finished creating response (return) [HttpResponse/create]");
        return resp;
    }

    public <K extends String, V extends String> void set(K key, V value) {
        System.out.println("[DEBUG][DEBUG] ARG key (K) ->  [HttpResponse/set] -->> " + key);
        System.out.println("[DEBUG][DEBUG] ARG value (V) ->  [HttpResponse/set] -->> " + value);
        this.headers.put(key, value);
    }

    // Utility functions

    private static Set<Integer> prepareCodes(Set<Integer> code) {
        code.add(StatusEnum._400_BAD_REQUEST.STATUS_CODE);
        code.add(StatusEnum._403_FORBIDDEN.STATUS_CODE);
        code.add(StatusEnum._500_INTERNAL_ERROR.STATUS_CODE);
        code.add(StatusEnum._405_METHOD_NOT_ALLOWED.STATUS_CODE);
        code.add(StatusEnum._404_NOT_FOUND.STATUS_CODE);
        code.add(StatusEnum._501_NOT_IMPLEMENTED.STATUS_CODE);
        code.add(StatusEnum._200_OK.STATUS_CODE);

        return code;
    }

    private static Set<String> prepareMessages(Set<String> msg) {
        msg.add(StatusEnum._400_BAD_REQUEST.MESSAGE);
        msg.add(StatusEnum._403_FORBIDDEN.MESSAGE);
        msg.add(StatusEnum._500_INTERNAL_ERROR.MESSAGE);
        msg.add(StatusEnum._405_METHOD_NOT_ALLOWED.MESSAGE);
        msg.add(StatusEnum._404_NOT_FOUND.MESSAGE);
        msg.add(StatusEnum._501_NOT_IMPLEMENTED.MESSAGE);
        msg.add(StatusEnum._200_OK.MESSAGE);

        return msg;
    }


}