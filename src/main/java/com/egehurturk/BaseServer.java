package com.egehurturk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

// consider adding the pool field for baseservice for threading

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
 * {@link ServerSocket}. Inner class {@link ConnectionManager} handles the
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

    /**
     * Accept any client that is connected to the BaseServer
     * {@code ServerSocket} by calling the {@code ServerSocket.accept()}
     * method.
     * <code>
     *     Socket client = server.accept();
     * </code>
     *
     * <p>Passes this client object to the {@link ConnectionManager} class
     * that handles the loop, input, and output of socket comminucation.
     *
     * @throws IOException          - IOException due to client acceptance
     * @see ConnectionManager
     * @see
     */
    public abstract void start() throws IOException; // MODIFIED THIS!!! RETURN VOID

    /**
     * Closes the socket channels for both {@code ServerSocket} object
     * of server and {@code Socket} object for client. This method should
     * be called if the server is going to be closed.
     * <code>
     *     HttpServer server = new HttpServer(...);
     *     try {
     *         server.close()
     *     }
     *     catch (IOException e) {
     *         e.printStackTrace();
     *     }
     *
     * </code>
     *
     * @throws IOException          - IOException due to closing of the client and server
     */
    public abstract void stop() throws IOException;

    // restart, reload

    /**
     * Manager class for handling {@code Socket} object client.
     * Any {@code Socket} object that is accepted by
     * {@code ServerSocket} is passed into this class
     * by constructor.
     *
     * <p> One convenient way to use this class is
     * creating an infinite loop inside the method that
     * constructs this class, and passes the {@code Socket} client
     * in the constructor.
     *
     * <p>The subclass of the {@link BaseServer} class should have
     * an inner class that handles the management of client,
     * possibly named <i>ConnectionManager</i> and extends this
     * base class
     *
     * <p>Implements the {@code Runnable} interface and a
     * {@code run} method for making the server threaded. Every
     * work that is done inside the {@code run} method executes
     * in another Thread.
     *
     *
     */
    public static class ConnectionManager implements Runnable {
        /**
         * The client {@code Socket} object that is connected
         * to the {@code ServerSocket}, via accept() method:
         *
         * <p>The client (Socket object) is passed into the
         * constructor of this class. {@link #in} and {@link #out}
         * is achieved via the {@code InputStream} and
         * {@code OutputStream} of the client.
         *
         * <code>
         *     try {
         *         Socket client = server.accept()
         *         ConnectionManager manager = new ConnectionManager(client);
         *     }
         *
         * </code>
         *
         */
        private Socket client;

        /**
         * Input for client socket. Everything
         * that the client sends to the server is
         * read by the {@code BufferedReader} object's
         * readline methods.
         */
        private BufferedReader in;

        /**
         * Output for client socket. Send anything
         * to client with calling the printline method of
         * {@code PrintWriter}.
         */
        private PrintWriter out;

        /**
         * Default constructor for this class.
         * @param socket         - the client socket that server accepts. All
         *                         input and output tasks are done with the socket's I/O streams.
         */
        protected ConnectionManager(Socket socket) {
            this.client = socket;
            System.out.println("[SERVER] New connection established: " + this.client + "\n\n");
        }

        /**
         * Services this thread's client by reading its {@link java.io.InputStream}
         * and outputting the input with {@link PrintWriter}.
         */
        @Override
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );
                out = new PrintWriter(client.getOutputStream(), true);

                boolean run = true;
                try {
                    while (run) {
                        String req = in.readLine();
                        if (req.equals("/quit")) {
                            run = false;
                            break;
                        }
                        out.println(req);
                    }
                }

                finally {
                    in.close();
                    out.close();
                    this.client.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Creates a new {@link ConnectionManager} class. Acts as
     * a factory method for the object, allows subclasses to
     * change behavior of the inner class {@link ConnectionManager}.
     *
     * <p>For extending the inner class, override this method
     * to return the local inner class of that subclass.
     *
     * @param cli                               - {@link Socket} object client
     * @return {@link ConnectionManager}        - new object for ConnectionManager
     */
    protected ConnectionManager createManager(Socket cli) {
        return new ConnectionManager(cli);
    }


}

/*
 * TODO: BaseServer class shouldn't be abstract
 * TODO: provide a psvm method that we can test out this server
 * TODO: provide a client class that we can use to connect to the server
 * TODO: Note: you can make the BaseServer class abstract if you want
 * TODO: Do you want to start your server from psvm, or from start() method? Decide on that
 * TODO: Consider adding a pool field for your baseserver class, for threading
 * TODO: To be static or not to be static? Decide on your fields.
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

/*
 * Description of classes in this project:
 *      - BaseServer: don't play with this. This is your base server. [SERIOUS]
 *      - App: Dummy. [DELETE]
 *      - Client: For testing purposes for baseserver class. Don't delete until your code is ready [MEH]
 *      - ClientApp: Class to start client. Again don't delete until your code is ready [MEH]
 *      - TestOneServer: A testing TCP Server. Again same as above. [MEH]
 *      - TestServer: [DELETE]
 */