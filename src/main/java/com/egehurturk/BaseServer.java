package com.egehurturk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Base Server for providing TCP/IPv4 Socket connection. Uses TCP as
 * 4th layer defined in <b>OSI</b> for a server-client based server
 *
 * <p> Simply creates a @code{ServerSocket} class for the base Server socket.
 * Configured via these fields:
 * <ul>
 *     <li>Server port</li>
 *     <li>Server host</li>
 *     <li>Backlog</li>
 * </ul>
 *
 * <p> In general, each connection request made of a client is accepted by
 * {@code ServerSocket}. Inner class {@code ConnectionListener} handles the
 * connection.
 *
 *
 * Uses {@code BufferedReader} and {@code PrintWriter} for communicating with
 * sockets. {@code InputStream} and {@code OutputStream} are received by the
 * socket itself, and then passed into BufferedReader and PrintWriter.
 *
 *
 * @see com.sun.net.httpserver.HttpsServer
 *
 * @author      Ege Hurturk
 * @version     1.0 - SNAPSHOT
 */

public abstract class BaseServer {
    // Abstract class

    /**
     * The server socket that client connects. Configured
     * with {@code serverPort}, {@code serverHost},
     * and {@code backlog}.
     *
     * <p>Pass in configurations in the
     * constructor of the subclass. Configurations
     * should be managed by <i>properties</i> file
     * later on.
     */
    protected ServerSocket server;

    /**
     * Server port that the server socket is attached on.
     * Client connects to this port whatever the
     * IP adress is.
     */
    protected int serverPort;

    /**
     * Server host that the server is running on. For now,
     * it is <i>localhost</i>. This can be changed in later
     * versions
     */
    protected InetAddress serverHost;
    /**
     * The number of pending connections the queue will hold.
     * More formally, the queued connection during the TCP handshake.
     *
     * <p>This class handles requests by a blocking {@code accept()}
     * method. This results in requests being queued by the server.
     *
     */
    protected int backlog;
    /**
     * The main reader that reads the client's socket output.
     * Stream is buffered as to provide efficient reading of
     *
     *
     * <p>This will be used for reading HTTP requests for clients
     * that are connected to the server
     *
     * <p> Achieved with the client Socket connection's {@code InputStream}.
     */
    protected BufferedReader in;
    /**
     * The main writer that writes for clients socket input.
     * This will be used for writing HTTP responses for
     * clients that are connected to the server.
     */
    protected PrintWriter out;

    /**
     * Meta information for logging, generating
     * reports, server maintenance. Values are stored
     * in {@code HashMap} as String, Object. Example usage:
     * {"Max Connected Clients": 4};
     *
     * In later versions, another class {@code MetaModel} can
     * be created to store metadata
     */
    protected static HashMap<String, Object> META;

    /**
     * Chained constructor for intializing with only port.
     * Passes default values for other arguments in the base
     * constructor
     *
     * @param   serverPort              - Server port for configuration
     *
     * @throws  UnknownHostException    - Required for {@code .getLocalHost()}.
     */
    public BaseServer (int serverPort) throws UnknownHostException {
        this(serverPort, InetAddress.getLocalHost(), 50);
    }

    /**
     * Empty constructor for BaseServer
     * Chains the default values for base constructor:
     * <ul>
     *     <li>serverPort: 9090</li>
     *     <li>serverHost: InetAddress localhost</li>
     *     <li>backlog: 50</li>
     * </ul>
     * @throws UnknownHostException     - Required for {@code .getLocalHost()}
     */
    public BaseServer () throws UnknownHostException {
        this(9090, InetAddress.getLocalHost(), 50);
    }

    /**
     * Base constructor that has all fields as arguments.
     * Checks for valid port, backlog.
     *
     * @param serverPort                    - Server port that ServerSocket listens on
     * @param serverHost                    - Server host that ServerSocket operates in ("localhost")
     * @param backlog                       - Number of pending requests in the queue
     *
     * @throws IllegalArgumentException     - Throws for value of port that is out of range, described below
     *
     */
    public BaseServer (int serverPort, InetAddress serverHost,
                       int backlog
                       ) {
        if (serverPort < 0 || serverPort > 65535) {
            // Server port should be between 0 and 65535, that is the default
            // for ServerSocket class
            throw new IllegalArgumentException(
                    "Port value out of range for server "
            );
        }

        if (backlog < 1) {
            backlog = 50;
        }
        try {
            // Initialize a new server
            this.server = new ServerSocket(serverPort, backlog, serverHost);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // Set fields
        this.serverPort = serverPort;
        this.serverHost = serverHost;
        this.backlog = backlog;
    }

    /**
     * Getter for serverPort.
     * @return serverPort - port number
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Getter for serverHost.
     * @return serverPort - server host
     */
    public InetAddress getServerHost() {
        return serverHost;
    }

    /**
     * Getter for backlog.
     * @return backlog - maximum number of pending connections in queue
     */
    public int getBacklog() {
        return backlog;
    }

    /**
     * Getter for input
     * @return in - BufferedReader object for input of {@code ServerSocket}
     */
    public BufferedReader getIn() {
        return in;
    }

    /**
     * Getter for output
     * @return out - PrintWriter object for printing to client
     */
    public PrintWriter getOut() {
        return out;
    }


    protected static class ConnectionManager implements Listener {

    }
}

/*
 * TODO: Create the Listener interface for managing clients
 * TODO: Create java docs for Listener interface
 * TODO: Create the ConnectionManager class
 * TODO: Create abstract methods / normal methods for BaseServer class
 */

/* Useful links:
 *      - Java.net package docs: https://docs.oracle.com/javase/8/docs/api/java/net/package-summary.html
 *      - Java.io package docs: https://docs.oracle.com/javase/8/docs/api/java/io/package-summary.html
 *      - Wiki Checked & Unchecked exceptions: https://en.wikibooks.org/wiki/Java_Programming/Checked_Exceptions#
 *      - Metadata design: https://stackoverflow.com/questions/29592216/how-should-i-store-metadata-in-an-object
 *      - Java doc standards: https://blog.joda.org/2012/11/javadoc-coding-standards.html
 *      - How to override inner classes?
 *          https://stackoverflow.com/questions/7588091/how-to-override-extend-an-inner-class-from-a-subclass
 *
 *      - HttpServer class in  com.sun.net.httpserver
 *      - Classes in  com.sun.net.httpserver
 *      - ServerImpl class in sun.net.httpserver
 */