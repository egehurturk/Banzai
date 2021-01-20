package com.egehurturk.handlers;

import com.egehurturk.httpd.HttpRequest;
import com.egehurturk.httpd.HttpResponse;

/**
 * Interface for connection handlers. Each and
 * every connection, in http protocol, should implement
 * the following {@code handle(request)} method. Handle can
 * summon other sub-handling methods, e.g, handle_GET, or handle_POST
 *
 * <p>every handler object should accept {@link HttpRequest} as an
 * argument, request that is parsed, and should return a {@link HttpResponse}
 * constructed
 */
public interface Handler {
    /**
     * Handler method for request, specifically for HTTP/HTTPS
     * @param request                       - client HTTP request parsed into {@link HttpRequest}
     * @param response                      - response object
     * @return                              - {@link HttpResponse} object that the result is going to be
     *                                        written into
     */
    // TODO: remove response from parameters
    HttpResponse handle(HttpRequest request, HttpResponse response);
}
