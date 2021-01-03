package com.egehurturk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 * @see HttpServer
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
     * Server name that is used in {@link HttpServer} class
     * in HTTP Protocol headers. Although usage is not limited for
     * HTTP, any child class that inherits this class should have
     * a name to run the server. Look for {@code q.txt} file for
     * future usages of the servers
     */
    protected String name;

    /**
     * Web Root that HTML files live in. This is a directory
     * that is the base URL. This directory will be considered and
     * used as a directory and {@link ConnectionManager} will look
     * for the path in this directory
     *
     * E.g.
     *  root_dir
     *      |- www
     *          | - index.html
     *      |- src
     *          | main
     *          ...
     *  When a path is requested, e.g localhost:9090/index.html, {@link ConnectionManager}
     *  will look in <i>www/index.html</i>.
     *
     *  Note that the webRoot should be an outer directory which does
     *  not live in src file. The standard directory structure includes
     *  that "www" specifies the root/www, ./src/main/www specifies a directory
     *  inside the src/main directory.
     */
    protected String webRoot;

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
    protected static String NAME_PROP = "server.name";
    protected static String WEBROOT_PROP = "server.webroot";

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
        this(serverPort, InetAddress.getLocalHost(), 50, "unnamed", "www");
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
     * @param name                          - Name of the server for running or stopping from CL
     * @param webRoot                       - Web Root of the server for URL processing
     *
     * @throws IllegalArgumentException     - Throws for value of port that is out of range, described below
     *
     */
    public BaseServer (int serverPort, InetAddress serverHost,
                       int backlog, String name, String webRoot
                       ) {
        if (serverPort < 0 || serverPort > 65535) {
            // Server port should be between 0 and 65535, that is the default
            // for ServerSocket class
            throw new IllegalArgumentException(
                    "Port value out of range for server"
            );
        }

        if (!isDirectory(webRoot)) {
            throw new IllegalArgumentException(
                "Web root directory not found. It should be placed in \"root/www\" where root" +
                        "is the top parent directory."
            );
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException(
                    "Length of server name is greater than 20. Try to make the " +
                            "name smaller"
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
        this.name = name;
        this.webRoot = webRoot;
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
     * Getter for name
     * @return  name - name of the server
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for webRoot
     * @return  webroot - web root of the server
     */
    public String getWebRoot() {
        return webRoot;
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
    public abstract void start() throws IOException;

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


    /**
     * Restarts the server. Not sure if it is needed
     */
    public abstract void restart();

    /**
     * Restarts the server. Not sure if it is needed
     */
    public abstract void reload();

    /**
     * Configures the server by the default properties file
     * which is <i>"server.properties"</i>
     * Note for future:
     *  - You have a constructor to configure, and this method to configure. Decide
     *    which one to use.
     *
     * @throws IllegalArgumentException     - where the web root directory is not correct
     */
    public void configureServer() {
        this.config = serveConfigurations(System.getProperties(), propertiesStream);
        try {
            this.serverHost = InetAddress.getByName(this.config.getProperty(HOST_PROP));
            this.serverPort = Integer.parseInt(this.config.getProperty(PORT_PROP));
            this.name = this.config.getProperty(NAME_PROP); // already a string

            if (!isDirectory(webRoot)) {
                throw new IllegalArgumentException(
                        "Web root directory not found. It should be placed in \"root/www\" where root" +
                                "is the top parent directory."
                );
            }

            this.webRoot = this.config.getProperty(WEBROOT_PROP);
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
            this.name = this.config.getProperty(NAME_PROP); // already a string

            if (!isDirectory(webRoot)) {
                throw new IllegalArgumentException(
                        "Web root directory not found. It should be placed in \"root/www\" where root" +
                                "is the top parent directory."
                );
            }
            this.webRoot = this.config.getProperty(WEBROOT_PROP);
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
     * Check if a directory exists in the file structure. Essential
     * for {@link HttpServer} for containing {@code HTML} files.
     *
     * @param dirPath               - Directory path where static and public
     *                              files live in. Default value is <i>www</i>.
     *                              See {@link #BaseServer(int, InetAddress, int, String, String)}
     *
     * @return  boolean             - returns true if a direcetory exists
     */
    protected boolean isDirectory(String dirPath) {
        return Files.isDirectory(Paths.get(dirPath));
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
                        if (req.isEmpty()) {}
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
                logger.info("Something wrong happened with client");
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

// end of class
