package com.egehurturk.handlers;

import com.egehurturk.httpd.HttpResponse;

/**
 * Interface for response types. Example response types may be a file
 * response type, which returns a file as a response {@link FileResponse}, or
 * a JSON response type which returns JSON data, {@link JsonResponse}, or even
 * an HTML renderer and returns dynamic HTML templates, {@link HTMLRenderer}
 *
 * <p>Every response type (e.g. file, json, html) should convert their results to
 * {@link HttpResponse} to send this response to client. Response types are called in a
 * class that is a handler, i.e. implements {@link Handler}.
 */
public interface ResponseType {
    HttpResponse toHttpResponse();
}
