package com.egehurturk.resolvers;

import com.egehurturk.HttpValues;
import com.egehurturk.exceptions.HttpRequestException;
import com.egehurturk.lifecycle.HttpRequest;

import java.util.Map;

/**
 * Resolves the HTTP Request, particularly {@link com.egehurturk.lifecycle.HttpRequest}.
 * Accepts the request in the constructor.
 *
 * <p>
 *     The main purpose of this class is to behave accordingly to the REQUEST.
 *     Request from client contains 3 essential information: status line (e.g GET /index HTTP/1.1),
 *     Headers (e.g Connection: Keep-Alive), and a body (for post requests). This class deals with
 *     headers. Example usage would be, e.g. when a request header contains {"Connection: Keep-Alive"},
 *     this resolver sets the server to keep the connection alive. There are plenty of headers
 *     that need to be "configured" or set when pushing the response to the client.
 * </p>
 *
 * @see com.egehurturk.HttpServer
 * @see com.egehurturk.lifecycle.HttpRequest
 */

public class RequestResolver {

    Map<String, String> headers;
    /**
     * Constructor with a {@link java.util.HashMap} get from
     * {@link HttpRequest#toMap()}.
     *
     * @param headerMap                 -  Header map
     * @throws HttpRequestException     - Exception for null http request
     */
    public RequestResolver(Map<String, String> headerMap) throws HttpRequestException {
        String method = headerMap.getOrDefault(HttpValues.Headers.METHOD, "");
        String path = headerMap.getOrDefault(HttpValues.Headers.URL_RESOURCE, "");
        String scheme = headerMap.getOrDefault(HttpValues.Headers.PROTOCOL, "");

        if (
                (method == null || method.isEmpty()) || (path == null || path.isEmpty()) || (scheme == null || scheme.isEmpty())
        ) {
            // TODO: Logging
            throw new HttpRequestException("Method, path, or protocol does not exist or null");
        }
        this.headers = headerMap;
    }



}
