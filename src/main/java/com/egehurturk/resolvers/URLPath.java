package com.egehurturk.resolvers;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A wrapper class for {@link java.net.URL}.
 */
public class URLPath {

    /**
     * {@link URL} address
     */
    public URL url;

    /**
     * Wrapper constructor
     * @param url                       - url as string
     * @throws MalformedURLException    - exception
     */
    public URLPath(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    /**
     * Add a relative path to the base URL
     *
     * "https://sendloop.com" + "index.html" -> "https://sendloop.com/index.html"
     *
     * @param path                      - relative path
     * @throws MalformedURLException    - exception
     */
    public void addRelativePath(String path) throws MalformedURLException {
        this.url = new URL(this.url, path);
    }

    /**
     * Getters
     */

    public String getProtocol() {
        return this.url.getProtocol();
    }

    public String getHost() {
        return this.url.getHost();
    }

    public int getPort() {
        return this.url.getPort();
    }

    public String getPath() {
        return this.url.getPath();
    }

    public String getQuery() {
        return this.url.getQuery();
    }

    public String getRef() {
        return this.url.getRef();
    }
}