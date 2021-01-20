package com.egehurturk.handlers;

import com.egehurturk.resolvers.URLPath;
import com.egehurturk.util.MethodEnum;

import java.util.Objects;

/**
 * Mapping class that maps a handler to a specific
 * method {@link MethodEnum} and to a path {@link URLPath}.
 * This is useful when adding handlers to URLs.
 */
public class HandlerTemplate {
    public MethodEnum method;
    public String path;
    public Handler handler;

    /**
     * Full constructor
     * @param method                - HTTP method that the handler is associated with
     * @param path                  - URL that the handler is mapped to
     * @param handler               - actual handler
     */
    public HandlerTemplate(MethodEnum method, String path, Handler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public MethodEnum getMethod() {
        return method;
    }

    public void setMethod(MethodEnum method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "HandlerTemplate{" +
                "method=" + method +
                ", path=" + path +
                '}';
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerTemplate that = (HandlerTemplate) o;
        return method == that.method &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }


}
