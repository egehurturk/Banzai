package com.egehurturk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * HTTP Server for providing HTTP connection. Uses TCP as
 * 4th layer defined in <b>OSI</b> for a server-client based server
 * This class is a child of {@link BaseServer} class which provides
 * abstraction over TCP/IPv4 communication. This class also overrides the
 * inner class {@link com.egehurturk.BaseServer.ConnectionManager}. A lot of
 * work (processing HTTP requests) is done inside the {@link com.egehurturk.BaseServer.ConnectionManager}
 * class.
 *
 * <p> Provides methods for parsing, reading, sending HTTP headers.
 * Configured via these fields:
 * <ul>
 *     <li>Server port</li>
 *     <li>Server host</li>
 *     <li>Backlog</li>
 * </ul>
 *
 * <p> In general, each connection request made of a client is accepted by
 * {@link ServerSocket}. Inner class {@link ConnectionManager} handles the
 * connection.
 *
 * Uses {@code BufferedReader} and {@code PrintWriter} for communicating with
 * sockets. {@code InputStream} and {@code OutputStream} are received by the
 * socket itself, and then passed into BufferedReader and PrintWriter.
 *
 * @author      Ege Hurturk
 * @version     1.0 - SNAPSHOT
 */

public class HttpServer extends BaseServer {
    /* Extends {@link BaseServer} class for base TCP/IPv4 connection activity */

    /**
     * Chained constructor for initalizing with only port.
     * @param serverPort                - server port that the HTTP server is running on
     * @throws UnknownHostException     - required for {@link InetAddress} to hold the host name (IPv4)
     */
    public HttpServer(int serverPort) throws UnknownHostException {
        super(serverPort);
    }

    public HttpServer() {
        super();
    }

    public HttpServer(int serverPort, InetAddress serverHost, int backlog) {
        super(serverPort, serverHost, backlog);
    }

    @Override
    public int getServerPort() {
        return super.getServerPort();
    }

    @Override
    public InetAddress getServerHost() {
        return super.getServerHost();
    }

    @Override
    public int getBacklog() {
        return super.getBacklog();
    }

    @Override
    public BufferedReader getIn() {
        return super.getIn();
    }

    @Override
    public PrintWriter getOut() {
        return super.getOut();
    }

    @Override
    public String getConfigPropFile() {
        return super.getConfigPropFile();
    }

    @Override
    public void setConfigPropFile(String configPropFile) {
        super.setConfigPropFile(configPropFile);
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }

    @Override
    public void restart() {
        super.restart();
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public void configureServer() {
        super.configureServer();
    }

    @Override
    public void configureServer(String propertiesFilePath) {
        super.configureServer(propertiesFilePath);
    }

    @Override
    protected Properties serveConfigurations(Properties userConfig, InputStream file) {
        return super.serveConfigurations(userConfig, file);
    }

    @Override
    protected ConnectionManager createManager(Socket cli) {
        return super.createManager(cli);
    }
}

// TODO: Think on your fields for only HTTP server class. Create constructors accordingly
//
