package com.egehurturk;

import com.egehurturk.exceptions.FileSizeOverflowException;
import com.egehurturk.exceptions.HttpRequestException;
import com.egehurturk.lifecycle.HttpRequest;
import com.egehurturk.lifecycle.HttpResponse;
import com.egehurturk.lifecycle.HttpResponseBuilder;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.MethodEnum;
import com.egehurturk.util.StatusEnum;
import com.egehurturk.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class HttpServer extends BaseServer implements Closeable {
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
    // TODO: Close the input stream
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
    public HttpServer() throws UnknownHostException {
        this(8080, InetAddress.getLocalHost(), 50, "unnamed", "www");
    }

    public HttpServer(String fileConfigPath) {
        // TODO: Implement with configuration file path constructor
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
        ExecutorService pool = Executors.newFixedThreadPool(500);
        logger.info("Server started on port " + this.serverPort);
        this.server = new ServerSocket(this.serverPort, this.backlog, this.serverHost);
        while (this.server.isBound() && !this.server.isClosed()) {
            Socket cli = this.server.accept();
            HttpManager manager = new HttpManager(cli, this.config);
            pool.execute(manager);
            logger.info("Connection established with " + cli + "");
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
     * <p>This class uses {@link com.egehurturk.lifecycle.HttpRequest} object and {@link HttpResponse}
     * to implement the lifecycle of http requests. Any error or exception
     * associated with these classes are located inside {@link com.egehurturk.exceptions}
     *
     */
    public class HttpManager implements Runnable, Closeable {
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
         *
         * Example usage:
         *
         * <pre>
         *
         *     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
         *     HttpRequest req = new HttpRequest(in);
         *     // access req
         *     System.out.println(req.method);
         *
         * </pre>
         */
        protected HttpRequest req = null;

        /**
         * Http Response for client
         *
         * Example usage:
         *
         * <pre>
         *
         *     Map<String, String> headers = new Map<>();
         *     headers.put("Content-Type", "text/html");
         *     headers.put("Content-Length", "216");
         *     String scheme = "HTTP/1.1";
         *     int code = 404;
         *     String message = "Not found"
         *     String body  = "<H1>request path does not exist</H1>" // byte
         *
         *     try {
         *         HttpResponse res = HttpResponse.create(headers, scheme, code, message, body); // set to the field
         *     }
         *     catch ({@link com.egehurturk.exceptions.HttpResponseException}) {...}
         *
         *
         * </pre>
         */
        protected HttpResponse res = null;

        public final String BAD_REQ = "400.html";
        public final String INDEX = "index.html";
        public final String _404_NOT_FOUND = "404.html";
        public final String _NOT_IMPLEMENTED = "501.html";

        public String FASTEST_IO = "readFile_IO";

        public String status;


        /**
         * Default constructor for this class.
         * @param socket                        - the client socket that server accepts. All
         *                                          input and output tasks are done with the socket's I/O streams.
         * @throws FileNotFoundException        - If webRoot is not a directory throw an error
         */
        public HttpManager(Socket socket, Properties config) throws IOException {
            this.client = socket;
            this.configuration = config;
            this._strWebRoot = config.getProperty(WEBROOT_PROP);
            if (!isDirectory(this._strWebRoot)) {
                logger.error("Web root is not a directory. Check if there exists a directory" +
                        "at root/www");
                throw new FileNotFoundException( "Web root directory not found. It should be" +
                        " placed in \"root/www\" where root" +
                        "is the top parent directory.");
            }
            this.webRoot = new File(this._strWebRoot);
        }

        @Override
        public void run() {
            OutputStream put = null;

            try {
                if (client.getInputStream() == null) {
                    close();
                    return;
                }
                this.in = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                );
                this.out = new PrintWriter(client.getOutputStream(), false);
                this.req = new HttpRequest(in);

                String method = this.req.method, path = this.req.path, scheme = this.req.scheme;
                switch (MethodEnum.valueOf(method)) {
                    case GET:
                        this.res = handle_GET(this.req);
                        break;
                    case POST:
                        this.res = handle_POST(this.req);
                        break;
                    default:
                        this.res = handle_NOT_IMPLEMENTED(this.req);
                }

                writeResponseToStream(this.res, this.out);
            } catch (IOException | HttpRequestException e) {
                logger.error("Something's gone wrong...");
                e.printStackTrace();
            } finally {
                try {
                   close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        private String resolvePath(String reqPath) {
            Path resolved = FileSystems.getDefault().getPath("");
            Path other = FileSystems.getDefault().getPath(reqPath);
            for (Path path: other) {
                if (!path.startsWith(".") && !path.startsWith("..")) {
                    resolved = resolved.resolve(path);
                }
            }

            if (resolved.startsWith("")) {
                // if empty then use index.html
                resolved = resolved.resolve(INDEX);
            }
            return resolved.toString();
        }

        private void writeResponseToStream(HttpResponse res, PrintWriter out) {
            // Todo: to be improved
            String body = new String(res.body);
            out.println(res.scheme + " " + res.code + " " + res.message);
            out.println(HeaderEnum.SERVER.NAME + name);
            out.println(HeaderEnum.DATE.NAME + res.headers.get(HeaderEnum.DATE.NAME));
            out.println(HeaderEnum.CONTENT_TYPE.NAME + res.headers.get(HeaderEnum.CONTENT_TYPE.NAME) + ";charset=\"utf-8\"");
            out.println(HeaderEnum.CONTENT_LENGTH.NAME + res.headers.get(HeaderEnum.CONTENT_LENGTH.NAME));
            out.println(HeaderEnum.CONNECTION.NAME + "close");
            out.println();
            out.println(body);
            out.println();
            out.flush();
        }

        private boolean isAbsolute(String path) {
            File f = new File(path);
            return f.isAbsolute();
        }

        private File prepareOutputWithMethod(HttpRequest req) {
            File outputFile = null;
            String resolvedFilePathUrl;
            if (req.method.equals(MethodEnum.GET.str)) {
                resolvedFilePathUrl = resolvePath(req.path);
                outputFile = new File(this._strWebRoot, resolvedFilePathUrl);
                if (!outputFile.exists()) {
                      this.status = StatusEnum._404_NOT_FOUND.MESSAGE;
                      outputFile = new File(this._strWebRoot, _404_NOT_FOUND);
                } else {
                    if (outputFile.isDirectory()) {
                        // /file -> index.html
                        outputFile = new File(outputFile, INDEX);
                    }
                    if (outputFile.exists()) {
                        this.status = StatusEnum._200_OK.MESSAGE;
                    } else {
                        this.status = StatusEnum._404_NOT_FOUND.MESSAGE;
                        outputFile = new File(this._strWebRoot, _404_NOT_FOUND);
                    }
                }
                // TODO: Handle absolute paths
            } else if (req.method.equals(MethodEnum.POST.str)) {
                // TODO: Handle later
            } else {
                this.status = StatusEnum._501_NOT_IMPLEMENTED.MESSAGE;
                outputFile = new File(this._strWebRoot, _NOT_IMPLEMENTED);
            }
            return outputFile;

        }

        public HttpResponse handle_GET(HttpRequest req) {
            String resolvedFilePathUrl;
            File outputFile = null;
            boolean statusReturned = false;
            // if "Host: " header is not present in the headers throw a 400 error
            // TODO: Remove constant
            if (!this.req.headers.containsKey("host")) {
                this.status = StatusEnum._400_BAD_REQUEST.MESSAGE;
                outputFile = new File(this._strWebRoot, BAD_REQ);
                statusReturned = true;
            }

            // bad request if path contains directory format
            if (!statusReturned) {
                if (req.path.contains("./") || req.path.contains("../")) {
                    this.status = StatusEnum._400_BAD_REQUEST.MESSAGE;
                    outputFile = new File(this._strWebRoot, BAD_REQ);
                    statusReturned = true;
                }

                else if (req.path.equals("/")) {
                    outputFile = prepareOutputWithMethod(this.req);
                }
                else if (isDirectory(req.path)){
                    this.status = StatusEnum._400_BAD_REQUEST.MESSAGE;
                    outputFile = new File(this._strWebRoot, BAD_REQ);
                    statusReturned = true;
                }
                else {
                    outputFile = prepareOutputWithMethod(this.req);
                }
            }

            byte[] bodyByte = null;
            // handle_GET, handle_POST functions
            switch (FASTEST_IO) {
                case "readFile_IO":
                    try {
                        bodyByte = Utility.readFile_IO(outputFile);
                    } catch (IOException  | FileSizeOverflowException e) {
                        this.logger.error("File size is too large");
                        e.printStackTrace();
                    }
                    break;
                case "readFile_NIO":
                    try {
                        bodyByte = Utility.readFile_NIO(outputFile);
                    } catch (IOException e) {
                        this.logger.error("Could not read the file");
                        e.printStackTrace();
                    }
                    break;
                case "readFile_NIO_DIRECT":
                    try {
                        bodyByte = Utility.readFile_NIO_DIRECT(outputFile);
                    } catch (IOException e) {
                        this.logger.error("Could not read the file");
                        e.printStackTrace();
                    }
                    break;
                default:
                    MappedByteBuffer _mappedBuffer = null;
                    try {
                        _mappedBuffer = Utility.read_NIO_MAP(outputFile);
                    } catch (IOException e) {
                        this.logger.error("Could not read the file");
                        e.printStackTrace();
                    }
                    if (_mappedBuffer != null) {
                        bodyByte = new byte[_mappedBuffer.remaining()];
                        _mappedBuffer.get(bodyByte);
                    } else {
                        this.logger.error("Cannot read file and store in memory");
                    }
                    break;
            }
            if (!statusReturned) {
                if (bodyByte == null) {
                    this.logger.error("Could not read file contents in memory");
                    this.status = StatusEnum._500_INTERNAL_ERROR.MESSAGE;
                    statusReturned = true;
                }
            }

            // <<<<<<<<<<<<< HEADER SETTING <<<<<<<<<<<<<<<<<<<<<
            ZonedDateTime now = ZonedDateTime.now();
            String dateHeader = now.format(DateTimeFormatter.ofPattern(
                    "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                    ZoneId.of("GMT")
                    )
            );
            String contentLang = "en_US", mimeType = null;
            try {
                mimeType = Files.probeContentType(outputFile.toPath());
            } catch (IOException e) {
                this.logger.error("Cannot determine the MIME type of file");
                e.printStackTrace();
            }

            HttpResponseBuilder builder = new HttpResponseBuilder();
            HttpResponse response = builder
                    .scheme("HTTP/1.1")
                    .code(StatusEnum.valueOf(Utility.enumStatusToString(status)).STATUS_CODE)
                    .message(StatusEnum.valueOf(Utility.enumStatusToString(status)).MESSAGE)
                    .body(bodyByte)
                    .setHeader(HeaderEnum.DATE.NAME, dateHeader)
                    .setHeader(HeaderEnum.SERVER.NAME, this.configuration.getProperty(NAME_PROP))
                    .setHeader(HeaderEnum.CONTENT_LANGUAGE.NAME, contentLang)
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+(bodyByte.length))
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, mimeType)
//                    .setHeader(HeaderEnum.CONTENT_ENCODING.NAME, "a")
                    .build();
            return response;
        }

        public HttpResponse handle_POST(HttpRequest req) {
            return new HttpResponse();
        }

        public HttpResponse handle_NOT_IMPLEMENTED(HttpRequest req) {
            return new HttpResponse();
        }

        @Override
        public void close() throws IOException {
            this.client.close();
            this.in.close();
            this.out.close();
        }

    }


    protected HttpManager createHttpManager(Socket cli, Properties config) {
        HttpManager manager = null;
        try {
            manager =  new HttpManager(cli, config);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Something wrong happened with creation of manager");
        }
        return manager;
    }
}

// ../resources/q.txt

