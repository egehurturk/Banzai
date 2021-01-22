package com.egehurturk.httpd;

import com.egehurturk.core.BaseServer;
import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.HandlerTemplate;
import com.egehurturk.handlers.HttpController;
import com.egehurturk.util.MethodEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

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
     * Required for configuring {@link #logger} field with the {@code log4j2.xml} file
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
    public boolean allowCustomUrlMapping =  false;

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

    private List<HandlerTemplate> handlers = new ArrayList<>();


    /**
     * Chained constructor for initializing with only port.
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
    public HttpServer() throws UnknownHostException {
        this(8080, InetAddress.getLocalHost(), 50, "unnamed", "www");
    }

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
    @Override
    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(500);
        logger.info("Server started on port " + this.serverPort);
        try {
            this.server = new ServerSocket(this.serverPort, this.backlog, this.serverHost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (this.server.isBound() && !this.server.isClosed()) {
            Socket cli = null;
            try {
                cli = this.server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("Connection established with " + cli + "");
            HttpController controller = new HttpController(cli, handlers);
            controller.setAllowForCustomMapping(this.allowCustomUrlMapping);
            pool.execute(controller);
        }
    }


    @Override
    public void stop() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void restart() {

    }

    @Override
    public void reload() {

    }

    public void addHandler(MethodEnum method, String path, Handler handler) {
        HandlerTemplate template = new HandlerTemplate(method, path, handler);
        handlers.add(template);
    }

    // <<<<<<<<<<<<< CORE <<<<<<<<<<<<<<


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
    public boolean isDirectory(String dirPath) {
        return super.isDirectory(dirPath);
    }

}


