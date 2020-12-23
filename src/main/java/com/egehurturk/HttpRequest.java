package com.egehurturk;

import java.util.Map;

/**
 * This server uses {@link HttpRequest} and {@link HttpResponse} to
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
    protected String scheme;

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
    protected byte[] body;

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
    protected String path;

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
    protected String method;

    /**
     * ========= ONLY IN POST REQUEST ==========
     *
     * A string representing the current encoding used to decode form submission data
     * You can write to this attribute to change the encoding used when accessing the form data.
     *
     */
    protected String contentEncoding;

    /**
     * ====== ONLY IN POST REQUEST ========
     *
     * A string representing the MIME type of the request, parsed from the
     * {@link HttpValues.Headers#CONTENT_TYPE}.
     */
    protected String contentType;

    /**
     * ======= ONLY IN POST REQUEST ========
     *
     * The length of the request body
     */
    protected String contentLength;

    /**
     * controls whether or not the network connection stays open after the current
     * transaction finishes. If the value sent is keep-alive, the connection is
     * persistent and not closed, allowing for subsequent requests to the same server to be done.
     *
     * Example usage is:
     *  "Connection: keep-alive"
     */
    protected String connection;

    /**
     * A dictionary containing all available HTTP headers.
     * Available headers depend on the client and server, but here are some examples:
     * <ul>
     *     <li>ACCEPT_LANGUAGE</li>
     *     <li>CACHE_CONTROL</li>
     *     <li>ACCEPT_LANGUAGE</li>
     *     <li>HOST</li>
     *     <li>ACCEPT_LANGUAGE</li>
     *     <li>ACCEPT</li>
     *     <li>ACCEPT_LANGUAGE</li>
     *     <li>CONTENT_LANGUAGE</li>
     *
     * </ul>
     */
    protected Map<String, String> META;




}

/* https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages */
/* TODO: Think about the methods for this class */
/* TODO: Create a method to enclose every field in a map. Call this headers. Remove META Map and make every header a field*/
