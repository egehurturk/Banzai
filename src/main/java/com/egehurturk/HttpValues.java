package com.egehurturk;

/**
 * An interface that stores HTTP Headers, values, status codes, etc.
 * All fields are declared as public, static, final
 */
public interface HttpValues {
    /**
     * HTTP Status codes for different situations
     * Here are the most common HTTP codes
     */
    public interface StatusCodeMessage {
        /**
         * OK when everything is OK.
         */
        String OK = " 200 OK";
        /**
         * Bad Request: Server cannot process due to
         *              client error
         */
        String BAD_REQ = " 400 Bad Request";
        /**
         * Forbidden: Server does not allow client to
         *            access the page because of not having
         *            necessary permissions for a resource
         */
        String FORBIDDEN = " 403 Forbidden";
        /**
         * Not Found: Y'all know what this is :-)
         */
        String NOT_FOUND = " 404 Not Found";
        /**
         * Method Not Allowed: The method that accesses the URL is
         *                     not allowed. E.g. accessing a page with
         *                     GET request that accepts only POST request
         */
        String METHOD_NOT_ALLOWED = " 405 Method Not Allowed";
        /**
         * Internal Server Error: A generic error message when something bad
         *                        happens inside the server
         */
        String INTERNAL_ERROR = " 500 Internal Server Error";
        /**
         * Not Implemented: Server lacks to fulfill the request. Usually this
         *                  implies future avaliability (e.g. a new feature)
         */
        String NOT_IMPLEMENTED = " 501 Not Implemented";
    }

    public interface Code {
        int OK = 200;
        int BAD_REQ = 400;
        int FORBIDDEN = 403;
        int NOT_FOUND = 404;
        int METHOD_NOT_FOUND = 405;
        int INTERNAL_ERROR = 500;
        int NOT_IMPLEMENTED = 501;
    }

    public interface Messages {
        String OK = "OK";
        String BAD_REQ = "Bad Request";
        String FORBIDDEN = "Forbidden";
        String NOT_FOUND = "Not Found";
        String METHOD_NOT_ALLOWED = "Method Not Allowed";
        String INTERNAL_ERROR = "Internal Server Error";
        String NOT_IMPLEMENTED = "Not Implemented";
    }

    /**
     * HTTP Methods for communicating with the server
     */
    public interface Method {
        /**
         * GET request occurs when someone access the URL endpoint
         * <code>
         *     /test/demo_form.php?name1=value1&name2=value2
         * </code>
         */
        String GET = "GET";
        /**
         * POST request occuers when the client attaches some data to the URL endpoint
         * <code>
         *     POST /test/demo_form.php HTTP/1.1
         *     Host: w3schools.com
         *     name1=value1&name2=value2
         * </code>
         */
        String POST = "POST";
        /**
         *  PUT request is used to send data to update a resource
         */
        String PUT = "PUT";
        String HEAD = "HEAD";
        String OPTIONS = "OPTIONS";
    }

    /**
     * Header values that are both requested and responds
     * These will be put in {@link java.util.Map}
     */
    public interface Headers {
        /**
         * =========== GENERAL ============
         *
         * Connection: controls whether or not the network connection stays open after the current
         *             transaction finishes. If the value sent is keep-alive, the connection is
         *             persistent and not closed, allowing for subsequent requests to the same server to be done.
         * <i>
         *     Connection: keep-alive
         *     Connection: close
         * </i>
         */
        String CONN = "Connection: ";

        /**
         *  ============= REQUEST =============
         *
         * Accept: advertises which content types, expressed as MIME types,
         *         the client is able to understand. Using content negotiation, the server then selects one of the proposals,
         *         uses it and informs the client of its choice with the Content-Type response header.
         *
         * <i>
         *    Accept: text/html
         *    Accept: image/*
         *
         *    // General default
         *    Accept: star/star
         * </i>
         */
        String ACCEPT = "Accept: ";

        /**
         * =============== REQUEST===============
         *
         * Encoding: The Accept-Encoding request HTTP header advertises which content encoding,
         *           usually a compression algorithm, the client is able to understand. Using content negotiation,
         *           the server selects one of the proposals, uses it and informs the client of its choice with the
         *           Content-Encoding response header {@link #CONTENT_ENCODING}.
         *  <i>
         *      Accept-Encoding: gzip
         *      Accept-Encoding: gzip, compress, br
         *  </i>
         */
        String ACCEPT_ENCODING = "Accept-Encoding: ";

        /**
         * ============= REQUEST ==============
         *
         * Language: advertises which languages the client is able to understand,
         *           and which locale variant is preferred. (By languages, we mean natural languages,
         *           such as English, and not programming languages.) Using content negotiation,
         *           the server then selects one of the proposals, uses it and informs the client of its choice
         *           with the Content-Language response header {@link #CONTENT_LANGUAGE}
         * <i>
         *     Accept-Language: de
         *     Accept-Language: de-CH
         * </i>
         */
        String ACCEPT_LANGUAGE = "Accept-Language: ";

        /**
         * ============== GENERAL ============
         *
         * Cache Control: holds directives (instructions) for caching in both requests and responses.
         * <i>
         *     Cache-Control: max-age=<seconds>
         *     Cache-Control: max-stale[=<seconds>]
         *     Cache-Control: min-fresh=<seconds>
         *     Cache-Control: no-cache
         *     Cache-Control: no-store
         *     Cache-Control: no-transform
         *     Cache-Control: only-if-cached
         * </i>
         */
        String CACHE_CONTROL = "Cache-Control: ";

        /**
         * ============== REQUEST ==============
         * Host: The Host request header specifies the host and port number of the server to which the
         *       request is being sent.
         *
         * <i>
         *     Host: developer.cdn.mozilla.net (port is optional)
         * </i>
         */
        String HOST = "Host: ";

        /**
         * =================== RESPONSE =============
         * Cookie: contains stored HTTP cookies associated with the server (i.e. previously sent by the server with th-
         *         e Set-Cookie header or set in Javascript using Document.cookie).
         *
         * Syntax:
         * <i>
         *     Cookie: name=value
         *     Cookie: name=value; name2=value2; name3=value3
         * </i>
         * Example:
         * <i>
         *     Cookie: PHPSESSID=298zf09hf012fh2; csrftoken=u32t4o3tb3gg43; _gat=1
         * </i>
         */
        String COOKIE = "Cookie: ";


        /**
         * =============== REQUEST =============
         * Resource: The URL path associated with the request. This is where the HTML file lives in
         * <i>
         *    GET [/index.html] HTTP/1.1
         * </i>
         */
        String URL_RESOURCE = "resource";

        /**
         * =============== REQUEST =============
         * Method: The method associated with the request.
         * <i>
         *      [GET] /index.html HTTP/1.1
         * </i>
         */
        String METHOD = "method";

        /**
         * =========== REQUEST ==============
         * Protocol: The protocol associated with the request.
         * <i>
         *     GET /index.html [HTTP/1.1]
         * </i>
         */
        String PROTOCOL = "protocol";

        /**
         * ============= RESPONSE ================
         * Server: describes the software used by the origin server that handled the request —
         *         that is, the server that generated the response.
         *
         * <i>
         *     Server: Apache/2.4.1 (Unix)
         * </i>
         */
        String SERVER = "Server: "; // config in properties file "server.name = ..."

        /**
         * ================ REQUEST ================
         * User Agent:  is a characteristic string that lets servers and network peers identify the application,
         *              operating system, vendor, and/or version of the requesting user agent.
         * <i>
         *     User-Agent: curl/7.64.1
         * </i>
         */
        String USER_AGENT = "User-Agent: ";

        /**
         * ============== RESPONSE ==========
         *
         * Date: contains the date and time at which the message was originated.
         * <i>
         *     Date: Wed, 21 Oct 2015 07:28:00 GMT
         * </i>
         */
        String DATE = "Date: ";

        /**
         * =============== GENERAL (POST IN REQUEST, NORMAL IN RESPONSE) ===============
         *
         * Content Type: is used to indicate the media type of the resource.
         *               In responses, a Content-Type header tells the client what the content type of the returned
         *               content actually is.
         *
         *  <i>
         *      Content-Type: text/html; charset=UTF-8
         *      Content-Type: multipart/form-data; boundary=something
         *  </i>
         */
        String CONTENT_TYPE = "Content-Type: ";

        /**
         * =============== GENERAL (POST IN REQUEST, NORMAL IN RESPONSE) ===============
         *
         * Content Length: indicates the size of the entity-body, in bytes, sent to the recipient.
         * <i>
         *     Content-Length: <length>
         * </i>
         */
        String CONTENT_LENGTH = "Content-Length: ";

        /**
         * =============== GENERAL (POST IN REQUEST, NORMAL IN RESPONSE) ===============
         *
         * Content Encoding:  is used to compress the media-type. When present, its value indicates which encodings
         *                    were applied to the entity-body. It lets the client know how to decode in order to
         *                    obtain the media-type referenced by the Content-Type header.
         *
         *  <i>
         *      Content-Encoding: gzip
         *      Content-Encoding: compress
         *      Content-Encoding: deflate
         *      Content-Encoding: identity
         *      Content-Encoding: br
         *
         *      // Multiple, in the order in which they were applied
         *      Content-Encoding: gzip, identity
         *      Content-Encoding: deflate, gzip
         *  </i>
         *
         *  <p>Directives:
         *  <ul>
         *      <li>gzip</li>
         *      <li>compress</li>
         *  </ul>
         */
        String CONTENT_ENCODING = "Content-Encoding: ";

        /**
         * =============== GENERAL (POST IN REQUEST, NORMAL IN RESPONSE) ===============
         *
         * Content Language: used to describe the language(s) intended for the audience,
         *                   so that it allows a user to differentiate according to the users' own preferred language.
         * <i>
         *     Content-Language: de-DE
         *     Content-Language: en-US
         *     Content-Language: de-DE, en-CA
         * </i>
         */
        String CONTENT_LANGUAGE = "Content-Language: ";

        String HTTP_V_1_1 = "HTTP/1.1";
    }
}

/*
Notes:
    - Headers that starts with Accept-... is sent by client, which expresses things they accept only
    - Server propose's a content_type response based on these Accept's.
 */
