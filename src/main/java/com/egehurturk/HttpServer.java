package com.egehurturk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
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

    public HttpServer(int serverPort, InetAddress serverHost, int backlog, String name, String webRoot) {
        super(serverPort, serverHost, backlog, name, webRoot);
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

//    public static class ConnectionManager implements Runnable {
//        /**
//         * The client {@code Socket} object that is connected
//         * to the {@code ServerSocket}, via accept() method:
//         *
//         * <p>The client (Socket object) is passed into the
//         * constructor of this class. {@link #in} and {@link #out}
//         * is achieved via the {@code InputStream} and
//         * {@code OutputStream} of the client.
//         *
//         * <code>
//         *     try {
//         *         Socket client = server.accept()
//         *         ConnectionManager manager = new ConnectionManager(client);
//         *     }
//         *
//         * </code>
//         *
//         */
//        private Socket client;
//
//        /**
//         * Input for client socket. Everything
//         * that the client sends to the server is
//         * read by the {@code BufferedReader} object's
//         * readline methods.
//         */
//        private BufferedReader in;
//
//        /**
//         * Output for client socket. Send anything
//         * to client with calling the printline method of
//         * {@code PrintWriter}.
//         */
//        private PrintWriter out;
//
//        /**
//         * Logger class for logging things
//         */
//        protected static Logger logger = LogManager.getLogger(BaseServer.ConnectionManager.class);
//
//        /**
//         * Default constructor for this class.
//         * @param socket         - the client socket that server accepts. All
//         *                         input and output tasks are done with the socket's I/O streams.
//         */
//        protected ConnectionManager(Socket socket) {
//            this.client = socket;
//        }
//
//        @Override
//        public void run() {
//            System.out.println("Hello, World");
//        }
//    }

   // @Override
    //protected ConnectionManager createManager(Socket cli) {
//        return new HttpServer.ConnectionManager(cli);
    //}
}

// TODO: Think on your fields for only HTTP server class. Create constructors accordingly
// TODO: implement this class. Rewrite the methods
