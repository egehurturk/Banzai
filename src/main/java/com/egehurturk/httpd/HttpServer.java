package com.egehurturk.httpd;

import com.egehurturk.core.BaseServer;
import com.egehurturk.exceptions.ConfigurationException;
import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.HandlerTemplate;
import com.egehurturk.handlers.HttpController;
import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.util.Methods;
import com.egehurturk.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP Server for providing HTTP connection. Uses TCP as
 * 4th layer defined in <b>OSI</b> for a server-client based server
 * This class is a child of {@link BaseServer} class which provides
 * abstraction over TCP/IPv4 communication. This class also overrides the
 * inner class {@link BaseServer.ConnectionManager}. A lot of
 * work (processing HTTP requests) is done inside the {@link BaseServer.ConnectionManager}
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

public class HttpServer extends BaseServer implements Closeable {
    /* Extends {@link BaseServer} class for base TCP/IPv4 connection activity */

    /**
     * @link org.apache.logging.log4j.Logger} interface for logging messages.
     */
    protected static Logger logger = LogManager.getLogger(HttpServer.class);

    /**
     * Property configuration file name.
     */
    protected static String CONFIG_PROP_FILE = "server.properties";
    /** Key descriptors for {@code .properties} file */
    protected static String PORT_PROP    = "server.port";
    protected static String HOST_PROP    = "server.host";
    protected static String NAME_PROP    = "server.name";
    protected static String WEBROOT_PROP = "server.webroot";
    public boolean allowCustomUrlMapping = false;


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
     * Storing handlers in an ArrayList as {@link HandlerTemplate}s.
     * {@link HttpController} class uses this to manage specific path
     * routing and serving HTML documents
     */
    private final List<HandlerTemplate> handlers = new ArrayList<>();


    /**
     * Chained constructor for initializing with only port.
     * @param serverPort                - server port that the HTTP server is running on
     * @throws UnknownHostException     - required for {@link InetAddress} to hold the host name (IPv4)
     */
    public HttpServer(int serverPort) throws UnknownHostException {
        this(serverPort, InetAddress.getLocalHost(), 50, "unnamed", "www", false);
    }

    /**
     * Empty constructor for BaseServer
     * Does nothing. Used for configuring from
     * an external properties file
     */
    public HttpServer() {}

    public HttpServer(String fileConfigPath) {
        setConfigPropFile(fileConfigPath);
        configureServer(fileConfigPath);
    }

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
     * @param debugMode                     - Whether the Debugging mode enabled or not
     *
     * @throws IllegalArgumentException     - Throws for value of port that is out of range, described below
     *
     */
    public HttpServer(int serverPort, InetAddress serverHost, int backlog, String name, String webRoot, boolean debugMode) {
        super(serverPort, serverHost, backlog, name, webRoot, debugMode);
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

    public static String getPortProp() {
        return PORT_PROP;
    }

    public static String getHostProp() {
        return HOST_PROP;
    }

    public static String getNameProp() {
        return NAME_PROP;
    }

    public static String getWebrootProp() {
        return WEBROOT_PROP;
    }

    public static void setPortProp(String portProp) {
        PORT_PROP = portProp;
    }

    public static void setHostProp(String hostProp) {
        HOST_PROP = hostProp;
    }

    public static void setNameProp(String nameProp) {
        NAME_PROP = nameProp;
    }

    public static void setWebrootProp(String webrootProp) {
        WEBROOT_PROP = webrootProp;
    }

    /**
     * Set allowance of any custom URL path mapping
     * @param allow allow custom URL Path routing
     */
    public void allowCustomUrlMapping(boolean allow) {
        this.allowCustomUrlMapping = allow;
    }

    /**
     * Setter for configuration property file. This is
     * used to configure the server from an external
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

    // >>>>>>>>>>>>>>>>>>>>> CORE >>>>>>>>>>>>>>>>>>>>
    /*
             _________________________________________
            / You are only young once, but you can    \
            \ stay immature indefinitely.             /
             -----------------------------------------
               \
                \
                    .--.
                   |o_o |
                   |:_/ |
                  //   \ \
                 (|     | )
                /'\_   _/`\
                \___)=(___/
    */

    /**
     * Starts the Executor service and server socket
     * Accepts any {@link Socket} client and summons a
     * {@link HttpController} class for managing clients.
     *
     * Default, {@link HttpHandler} class is added to {@link #handlers}
     */
    @Override
    public void start() {

        if (checkFields()) {
            logger.error("Server is not properly initialized"); logger.error("Configuration error");
            logger.error("Stopping execution");
            // uhh, tons of try/catches
            try {
                this.propertiesStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return;
        }
        try {
            HttpHandler handler = new HttpHandler(this.getConfig());
            handler.setDebugMode(this.debugMode);
            addHandler(Methods.GET, "/*", handler);
        } catch (FileNotFoundException er) {
            logger.error(er.getMessage());
            try {
                this.propertiesStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return;
        }
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try {
            this.server = new ServerSocket(this.serverPort, this.backlog, this.serverHost);
            System.out.println();
            System.out.println("Banzai version 1.0, using configuration " + this.getConfigPropFile());
            System.out.println("Starting Banzai server at port " + this.serverPort);
            System.out.println("Quit the server with CONTROL-C (^C)");
            System.out.println();
        } catch (IOException e) {
            logger.error("Server could not be instantiated (probably due to port conflict) \n\n" +  e.getClass().getCanonicalName() );
            close();
            return;
        }
        while (this.server.isBound() && !this.server.isClosed()) {
            Socket cli = null;
            try {
                cli = this.server.accept();
                logger.info("Connection established with  " + cli.getInetAddress() + ":" + cli.getPort());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            HttpController controller = new HttpController(cli, handlers);
            controller.setAllowForCustomMapping(this.allowCustomUrlMapping);
            if (ignoredPaths.size() >= 1)
                controller.ignore(ignoredPaths);
            else
                System.out.println("No ignored paths");
            pool.execute(controller);
        }
    }


    /**
     * Stops the server
     */
    @Override
    public void stop() {
        logger.info("Stopping server...");
        close();
    }

    /**
     * Close any open socket connections, streams, etc.
     */
    @Override
    public void close() {
        try {
            this.server.close();
            this.propertiesStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restart() {}
    @Override
    public void reload() {}

    /**
     * Adds a Handler associated with method, path
     * @param method  HTTP Method (e.g. "GET")
     * @param path    URL Path    (e.g. "/hello")
     * @param handler Handler     (any class that implements {@link Handler}
     */
    public void addHandler(Methods method, String path, Handler handler) {
        HandlerTemplate template = new HandlerTemplate(method, path, handler);
        handlers.add(template);
    }

    public void ignore(Methods method, String path) {
        try {
            if (path == null || path.equals(""))
                throw new IllegalArgumentException("Path to be blocked cannot be empty or null");
            if (method == null)
                throw new IllegalArgumentException("Method to block cannot be null. See com.egehurturk.util.Methods for supported HTTP methods");
            this.ignoredPaths.add(Pair.makePair(method, path));
        } catch (IllegalArgumentException err) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            err.printStackTrace(pw);
            String cause = sw.toString().substring(0, sw.toString().indexOf(err.getMessage()));
            logger.error(cause + err.getMessage());
        }

    }

    // <<<<<<<<<<<<< CORE <<<<<<<<<<<<<<


    /**
     * {@link BaseServer#configureServer()}
     */
    @Override
    public void configureServer() throws ConfigurationException {
        super.configureServer();
    }

    /**
     * {@link BaseServer#configureServer(String)}
     */
    @Override
    public void configureServer(String propertiesFilePath) {
        try {
            super.configureServer(propertiesFilePath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
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
    public boolean isDirectory(String dirPath) {
        return super.isDirectory(dirPath);
    }

    /*
     * Helper method to check fields of this class
     */
    private boolean checkFields() {
        return this.serverHost == null || this.serverPort == 0 || this.webRoot == null || this.name == null;
    }

}


