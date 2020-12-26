package com.egehurturk;

import com.egehurturk.lifecycle.HttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

public final class HttpServer extends BaseServer {
    /* Extends {@link BaseServer} class for base TCP/IPv4 connection activity */

    /**
     * @link org.apache.logging.log4j.Logger} interface for logging messages.
     */
    protected static Logger logger = LogManager.getLogger(HttpServer.class);
    /**
     * Required for configurating {@link #logger} field with the {@code log4j2.xml} file
     */
    protected static LoggerContext context =  ( org.apache.logging.log4j.core.LoggerContext ) LogManager
                                                                        .getContext(false);
    static  {
        // configure from file (XML)
        context.setConfigLocation( new File("src/main/resources/log4j2.xml").toURI()) ;
    }

    /**
     * Property configuration file name.
     */
    protected static String CONFIG_PROP_FILE = "server.properties";

    /** Key descriptors for {@code .properties} file */
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
     * Chained constructor for initalizing with only port.
     * @param serverPort                - server port that the HTTP server is running on
     * @throws UnknownHostException     - required for {@link InetAddress} to hold the host name (IPv4)
     */
    public HttpServer(int serverPort) throws UnknownHostException {
        this(serverPort, InetAddress.getLocalHost(), 50, "unnamed", "www");
    }

    /**
     * Empty constructor for BaseServer
     * Does nothing. Used for configuring from
     * an external properties file
     */
    public HttpServer() {}

    /**
     * Base constructor that has all fields as arguments. Is used for manually
     * configuring fields (Port, host, backlog). Uses super class {@link BaseServer}.
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
    public HttpServer(int serverPort, InetAddress serverHost, int backlog, String name, String webRoot) {
        super(serverPort, serverHost, backlog, name, webRoot);
    }

    /**
     * Getters
     */

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
    public String getName() {
        return super.getName();
    }

    @Override
    public String getWebRoot() {
        return super.getWebRoot();
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

    /**
     * Setter for configuration property file. This is
     * used to configurate the server from an external
     * properties file.
     * @param configPropFile        - name of file
     */
    @Override
    public void setConfigPropFile(String configPropFile) {
        super.setConfigPropFile(configPropFile);
    }

    /**
     * Setter for {@link #logger} field
     * @param logger                - {@link org.apache.logging.log4j.Logger} instance
     */
    public static void setLogger(Logger logger) {
        HttpServer.logger = logger;
    }

    // <<<<<<<<<<<<<<<<< CORE <<<<<<<<<<<<<<<<<<
    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }

    @Override
    public void restart() {

    }

    @Override
    public void reload() {

    }

    // >>>>>>>>>>>>>>>>>> CORE >>>>>>>>>>>>>>>>>>

    /**
     * {@link BaseServer#configureServer()}
     */
    @Override
    public void configureServer() {
        super.configureServer();
    }

    /**
     * {@link BaseServer#configureServer(String)}
     */
    @Override
    public void configureServer(String propertiesFilePath) {
        super.configureServer(propertiesFilePath);
    }

    /**
     * {@link BaseServer#serveConfigurations(Properties, InputStream)} )}
     */
    @Override
    protected Properties serveConfigurations(Properties userConfig, InputStream file) {
        return super.serveConfigurations(userConfig, file);
    }

    /**
     * {@link BaseServer#isDirectory(String)} )}
     */
    @Override
    protected boolean isDirectory(String dirPath) {
        return super.isDirectory(dirPath);
    }

    /**
     * Manager class for handling {@link Socket} object client. Using
     * the {@link BufferedReader}, this class access the HTTP Request (since HTTP)
     * and parses it. Any {@link Socket} object that is accepted by
     * {@link ServerSocket} is passed into this class
     * by constructor.
     *
     * <p> One convenient way to use this class is
     * creating an infinite loop inside the method that
     * constructs this class, and passes the {@link Socket} client
     * in the constructor.
     *
     * <p>This class is responsible for managing the connection. The
     * {@link HttpServer#start()} creates an instances of this class and
     * with a client {@link Socket} argument, and then runs this
     * with {@link java.util.concurrent.ExecutorService#execute(Runnable)}
     * method. This makes the server threaded
     *
     * <p>Implements the {@link Runnable} interface and a
     * {@code run} method for making the server threaded. Every
     * work that is done inside the {@code run} method executes
     * in another Thread.
     *
     * <p>This class uses {@link com.egehurturk.lifecycle.HttpRequest} object and TODO:link response class
     * to implement the lifecycle of http requests. Any error or exception
     * associated with these classes are located inside {@link com.egehurturk.exceptions}
     *
     */
    public class HttpManager implements Runnable {
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
         * The client is assumed as a browser, or a CLI tool that sends requests
         * to the server.
         */
        private Socket client;

        /**
         * Input for client socket. Everything
         * that the client requests to the server is
         * read by the {@code BufferedReader} object's
         * readline methods.
         */
        private BufferedReader in;

        /**
         * Output for client socket. Send anything
         * to client with calling the {@link PrintWriter#println()} method of
         * {@code PrintWriter}.
         */
        private PrintWriter out;

        /**
         * Configuration file for accessing
         * {@link #webRoot}
         */
        private Properties configuration;

        /**
         * Web Root that HTML files live in. This is a directory
         * that is the base URL. This directory will be considered and
         * used as a directory and {@link HttpManager} will look
         * for the path in this directory
         *
         * E.g.
         *  root_dir
         *      |- www
         *          | - index.html
         *      |- src
         *          | main
         *          ...
         *  When a path is requested, e.g localhost:9090/index.html, {@link HttpManager}
         *  will look in <i>www/index.html</i>.
         *
         *  Note that the webRoot should be an outer directory which does
         *  not live in src file. The standard directory structure includes
         *  that "www" specifies the root/www, ./src/main/www specifies a directory
         *  inside the src/main directory.
         */
        private File webRoot;

        /**
         * String representation of {@link #webRoot}
         */
        private String _strWebRoot;

        /**
         * Logging manager
         */
        protected Logger logger = LogManager.getLogger(HttpManager.class);

        /**
         * Http request for client.
         */
        protected HttpRequest req;
        // TODO: response

        /**
         * Default constructor for this class.
         * @param socket                        - the client socket that server accepts. All
         *                                          input and output tasks are done with the socket's I/O streams.
         * @throws FileNotFoundException        - If webRoot is not a directory throw an error
         */
        protected HttpManager(Socket socket, Properties config) throws IOException {
            this.client = socket;
            this.configuration = config;
            this._strWebRoot = config.getProperty(WEBROOT_PROP);
            if (!isDirectory(this._strWebRoot)) {
                throw new FileNotFoundException( "Web root directory not found. It should be" +
                        " placed in \"root/www\" where root" +
                        "is the top parent directory.");
                // TODO: Logging
            }
            this.webRoot = new File(this._strWebRoot);
            this.req = new HttpRequest(new BufferedReader(
                    new InputStreamReader(this.client.getInputStream())
            ));
            // TODO: Response

        }

        // TODO: implement this
        @Override
        public void run() {

        }

        // TODO: implement this
        private byte[] readFile(File file) throws IOException {return new byte[5];}
    }


    protected HttpManager createHttpManager(Socket cli, Properties config) {
        HttpManager manager = null;
        try {
            manager =  new HttpManager(cli, config);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Logging
        }
        return manager;
    }
}

// TODO: Think on your fields for only HTTP server class. Create constructors accordingly
// TODO: implement this class. Rewrite the methods
