package com.egehurturk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

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
     * {@link org.apache.logging.log4j.Logger} interface for logging messages
     * Initialized to null defaultly, override the value in subclasses,
     * this can cause {@link NullPointerException}
     */
    protected static Logger logger = null;

    /**
     * Required for configurating {@link #logger} field with the {@code log4j2.xml} file
     */
    protected static LoggerContext context = ( org.apache.logging.log4j.core.LoggerContext ) LogManager
                                                                        .getContext(false);

    // set default log4j2 config location to the ../resources/log4j2.xml
    static  {
        context.setConfigLocation( new File("src/main/resources/log4j2.xml").toURI()) ;
    }


    /**
     * Config properties imported from {@code .properties}
     * file. Stored as {@link Properties}
     */
    protected Properties config;

    /**
     * Properties file name
     */
    protected static String CONFIG_PROP_FILE = "server.properties";

    /**
     * Properties config places
     */
    protected static String PORT_PROP = "server.port";
    protected static String HOST_PROP = "server.host";

    /**
     * File location of {@code .properties} file located
     * in {@link InputStream} to perform IO operations (specifically
     * reading the properties file) on the data.
     *
     * <p>All of the settings are required in the properties
     * file, located in the resources directory in the standard
     * Maven project DIR.
     */
    protected InputStream propertiesStream = ClassLoader.getSystemClassLoader()
            .getResourceAsStream( CONFIG_PROP_FILE );

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
     * Does nothing. Used for configuring from
     * an external properties file
     */
    public BaseServer () {}

    /**
     * Base constructor that has all fields as arguments. Is used for manually
     * configuring fields (Port, host, backlog).
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
     * Getter for config prop file
     * @return out - PrintWriter object for printing to client
     */
    public String getConfigPropFile() {
        return CONFIG_PROP_FILE;
    }

    /**
     * Setter for {@link #CONFIG_PROP_FILE}
     * @param configPropFile        - name of file
     */
    public void setConfigPropFile(String configPropFile) {
        CONFIG_PROP_FILE = configPropFile;
    }

    /**
     * Setter for {@link #logger} field
     * @param logger                - {@link org.apache.logging.log4j.Logger} instance
     */
    public static void setLogger(Logger logger) {
        BaseServer.logger = logger;
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
     * Configures the server by the default properties file
     * which is <i>"server.properties"</i>
     * Note for future:
     *  - You have a constructor to configure, and this method to configure. Decide
     *    which one to use.
     */
    public void configureServer() {
        this.config = serveConfigurations(System.getProperties(), propertiesStream);
        try {
            this.serverHost = InetAddress.getByName(this.config.getProperty(HOST_PROP));
            this.serverPort = Integer.parseInt(this.config.getProperty(PORT_PROP));
            logger.info("");
        } catch (UnknownHostException e) {
            System.err.println("Server name " + HOST_PROP + "that you passed into the configurations file " +
                    "(<name>.properties) is not valid. Make sure the host name exists or valid, or change" +
                    "the property. ");
        }
    }

    /**
     * Configures the server via the property file name passed in as argument.
     * This method is equal to setting the {@link #CONFIG_PROP_FILE} with the
     * {@link #setConfigPropFile(String)} method.
     *
     * @param propertiesFilePath        - Property file name (path)
     */
    public void configureServer(String propertiesFilePath) {
        this.config = serveConfigurations(System.getProperties(), ClassLoader.getSystemClassLoader()
                                                                    .getResourceAsStream(propertiesFilePath));
        try {
            this.serverHost = InetAddress.getByName(this.config.getProperty(HOST_PROP));
            this.serverPort = Integer.parseInt(this.config.getProperty(PORT_PROP));
        } catch (UnknownHostException e) {
            System.err.println("Server name " + HOST_PROP + "that you passed into the configurations file " +
                    "(<name>.properties) is not valid. Make sure the host name exists or valid, or change" +
                    "the property. ");
        }
    }

    /**
     * Populates the necessary fields (or properties) from the source
     * {@link InputStream} properties file, and the {@link Properties} user config.
     * If the properties file is missing, throws an {@link IOException} error.
     *
     * @param userConfig            - {@link Properties} file that has properties
     * @param file                  - {@link InputStream} file resource
     * @return serverConfig         - {@link Properties} new properties file
     *
     */
    protected Properties serveConfigurations(Properties userConfig, InputStream file) {
        Properties serverConfig = new Properties();
        try {
            if (file == null) {
                throw new IOException("System Configuration Error: Are you sure that a properties" +
                        " file is located under resources folder as stated in standard Maven " +
                        " directory template?");
            }

            serverConfig.load(file);

            userConfig.keySet().
                    forEach(val -> {
                        String key = (String) val;
                        String value = userConfig.getProperty(key);
                        if (value != null)
                            serverConfig.put(key, value);
                    });
        }
        catch (IOException err) {
            System.err.println("System Configuration Error: Are you sure that a properties" +
                               " file is located under resources folder as stated in standard Maven " +
                               " directory template?");
        }
        return serverConfig;
    }

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
         * Logger class for logging things
         */
        protected static Logger logger = LogManager.getLogger(ConnectionManager.class);

        /**
         * Default constructor for this class.
         * @param socket         - the client socket that server accepts. All
         *                         input and output tasks are done with the socket's I/O streams.
         */
        protected ConnectionManager(Socket socket) {
            this.client = socket;
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

                try {
                    while (true) {
                        String req = in.readLine();
                        logger.info("Client says " + req);
                        if (req.equals("/quit")) {
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
                System.err.println("Something wrong happened with the client socket " +
                        ", status code = 1");
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
Initserver method that initializes server with either (overload):
    - Properties file
    - or void
Call this with constructors
 */

/*
when do you want to log things?
Log things at subclasses, particularly HTTPServer
* Server Starts (info)
* Server stops (info)
* Client Conencts (info)
* Client leaves (info)
* Server restarts (info)
* Log Errors (method name) (error)
* On each request (GET, POST, PUT, DELETE) (info)
*
 */

/*
Note: I've used logging in ClientManager and TestOneServer thus far.
 */