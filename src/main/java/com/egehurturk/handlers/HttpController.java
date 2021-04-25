package com.egehurturk.handlers;

import com.egehurturk.exceptions.HttpRequestException;
import com.egehurturk.exceptions.MethodNotAllowedException;
import com.egehurturk.httpd.HttpRequest;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.httpd.HttpServer;
import com.egehurturk.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manager class for handling {@link java.net.Socket} object client. Using
 * the {@link java.io.BufferedReader}, this class access the HTTP Request (since HTTP)
 * and parses it. Any {@link java.net.Socket} object that is accepted by
 * {@link java.net.ServerSocket} is passed into this class
 * by constructor.
 *
 * <p> One convenient way to use this class is
 * creating an infinite loop inside the method that
 * constructs this class, and passes the {@link java.net.Socket} client
 * in the constructor.
 *
 * <p>This class is responsible for managing the connection. The
 * {@link HttpServer#start()} creates an instances of this class and
 * with a client {@link java.net.Socket} argument, and then runs this
 * with {@link java.util.concurrent.ExecutorService#execute(Runnable)}
 * method. This makes the server threaded
 *
 * <p>Implements the {@link Runnable} interface and a
 * {@code run} method for making the server threaded. Every
 * work that is done inside the {@code run} method executes
 * in another Thread.
 *
 * <p>This class uses {@link HttpRequest} object and {@link HttpResponse}
 * to implement the lifecycle of http requests. Any error or exception
 * associated with these classes are located inside {@link com.egehurturk.exceptions}
 *
 */
public class HttpController implements Closeable, Runnable {
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
     * read by the {@link BufferedReader} object's
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
     * Store ignored (blocked) paths with the relevant
     * methods.
     *
     * @see #ignore(List)
     */
    private List<Pair<Methods, String>> ignored = new ArrayList<>();

    /**
     * Logging manager
     */
    protected Logger logger = LogManager.getLogger(HttpController.class);

    private boolean debugMode;

    /**
     * List of handlers that are assigned to some method and path
     * Stored as {@link HandlerTemplate}
     */
    public List<HandlerTemplate> handlers;
    private HashMap<Method, Pair<String, Methods>> methodHandlers = new HashMap<>();
    private Boolean allowForCustomMapping = false;

    /**
     * Default constructor for this class.
     * @param socket                        - the client socket that server accepts. All
     *                                          input and output tasks are done with the socket's I/O streams.
     */
    public HttpController(Socket socket, List<HandlerTemplate> handlers) {
        this.client = socket;
        this.handlers = handlers;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * About handlers:
     * <p>
     *     Built-in to system, there is a one {@link HttpHandler} installed
     *     that is on path <i>/*</i>, which is the default path. This handler
     *     listens to all requests (specifically GET) and then responds to the request
     *     from the given request's path and finding the relevant HTML
     *     file from the local system path.
     *
     *     However, if there exists a handler that handles, e.g. GET /hello, then this
     *     new handler will override the existing behaviour of the default handler, {@link HttpHandler}
     *
     *     Priority levels are:
     *     <ul>
     *         <li>
     *            MOST PRIORITIZED: /<your:path>
     *         </li>
     *         <li>
     *             LEAST PRIORITIZED: /* (default)
     *         </li>
     *     </ul>
     * </p>
     */
    @Override
    public void run() {
        try {
            // if client input stream is null close the server gracefully
            if (client.getInputStream() == null) {
                close();
                return;
            }
            this.in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );
            this.out = new PrintWriter(client.getOutputStream(), false);
            // parse request
            HttpRequest req = new HttpRequest(in);
            boolean foundHandler = false;
            HttpResponse res = new HttpResponse(this.out);

            // get all handlers that implements {@code req.getMethod}. E.g, this list can contain all handlers
            // that accepts GET request
            List<HandlerTemplate> methodTemplates = findHandlerTemplateListFromMethod(req.getMethod());
            if (methodTemplates.isEmpty()) {
                throw new MethodNotAllowedException("Method is not allowed at path " + req.getPath(), 405, "Method Not Allowed");
            }

            /* Handler-based iteration */
            if (this.allowForCustomMapping) {
                // iterate over all handlers and find the handler template that is assigned to path
                for (HandlerTemplate templ: methodTemplates) {
                    // if exists
                    if (templ.path.equals(req.getPath())) {
                        // we can use req.getPath() and templ.path interchangeably in here since they are the same
                        if (isPathIgnored(templ.method, templ.path)) {
                            respondWith404(client.getOutputStream(), req);
                            foundHandler = true;
                            break;
                        }
                        res = templ.handler.handle(req, res); // let handler to handle the request
                        try {
                            boolean suc = res.send();
                            if (suc)
                                logger.info("[" + req.getMethod() + " " + req.getPath() + " " + req.getScheme() + "] " + res.getCode());
                        } catch (NullPointerException pointerException) {
                           respondWith500(client.getOutputStream(), req);
                        }
                        foundHandler = true; // we found a handler
                        break;
                    }
                }
            }

            /* method-Handler-based iteration */
            if (!foundHandler && this.allowForCustomMapping && methodHandlers.size() >= 1) {
                for (Map.Entry<Method, Pair<String, Methods>> entry: this.methodHandlers.entrySet()) {
                    Method method = entry.getKey();
                    String path = entry.getValue().getFirst();
                    Methods methods = entry.getValue().getSecond();

                    Utility.debug(debugMode,"Method name: " + method.getName(), logger);
                    Utility.debug(debugMode,"Path: " + path, logger);
                    Utility.debug(debugMode,"HTTP Method: " + methods, logger);

                    if (path.equals(req.getPath()) && methods.str.equals(req.getMethod())) {
                        if (isPathIgnored(methods, path)) {
                            respondWith404(client.getOutputStream(), req);
                            foundHandler = true;
                            break;
                        }
                        try {
                            method.setAccessible(true);
                            res = (HttpResponse) method.invoke(null, req, res);

                            if (res == null) {
                                respondWith500(client.getOutputStream(), req);
                            } else {
                                boolean suc = res.send();
                                if (suc)
                                    logger.info("[" + req.getMethod() + " " + req.getPath() + " " + req.getScheme() + "] " + res.getCode());
                            }

                        } catch (IllegalAccessException | InvocationTargetException  e) {
                            logger.error("Error while invoking HandlerMethod " + method.getName() + ": "  + e.getMessage());
                            respondWith500(client.getOutputStream(), req);
                        } catch (IllegalArgumentException err) {
                            logger.error("HandlerMethod's should be static.");
                            respondWith500(client.getOutputStream(), req);
                        }
                        foundHandler = true;
                        break;
                    }
                }
            }

            /* default iteration */
            if (!foundHandler) {
                // check for default handler (all paths)
                for (HandlerTemplate template: methodTemplates) {
                    if (template.path.equals("/*")) {
                        // do not use template.path here since it will be /*.
                        // check if request path is ignored
                        if (isPathIgnored(template.method, req.getPath())) {
                            respondWith404(client.getOutputStream(), req);
                            break;
                        }
                        res = template.handler.handle(req, res);
                        boolean suc = false;
                        try {
                            suc = res.send();
                        } catch (NullPointerException nullPointerException) {
                            respondWith500(client.getOutputStream(), req);
                        }
                        if (suc)
                            logger.info("[" + req.getMethod() + " " + req.getPath() + " " + req.getScheme() + "] " + res.getCode());
                        break;
                    }
                }

            }

        } catch (IOException e) {
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), false);
                FileResponse response = new FileResponse(ClassLoader.getSystemClassLoader().getResourceAsStream("500.html"), writer);
                respond(response.toHttpResponse(Status.valueOf("Internal Server Error"), this.out));
                writer.close();
            } catch (IOException ioException) {
                logger.error("IOException thrown while accessing client's stream: [" + ioException.getMessage() + "]");
            }
        }
        catch (HttpRequestException e) {
            logger.error(e.getMessage() + " [Code: " + e.code + ", Message: " + e.message + "]");
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), false);
                FileResponse response = new FileResponse(ClassLoader.getSystemClassLoader().getResourceAsStream( e.code + ".html"), writer);
                respond(response.toHttpResponse(Status.valueOf(Utility.enumStatusToString(e.message)), this.out));
                writer.close();
            } catch (IOException ioException) {
                logger.error("IOException thrown while accessing client's stream: [" + ioException.getMessage() + "]");
            }
        }
        finally {
            try {
                close();
            } catch (IOException e) {
                logger.error("Cannot close client socket's input/output streams: [" + e.getMessage() + "]");
            }
        }
    }

    private void respondWith404(OutputStream out, HttpRequest req) {
        PrintWriter writer = new PrintWriter(out, false);
        FileResponse response = new FileResponse(ClassLoader.getSystemClassLoader().getResourceAsStream("404.html"), writer);
        respond(response.toHttpResponse(Status._404_NOT_FOUND, this.out));
        writer.close();
        logger.info("[" + req.getMethod() + " " + req.getPath() + " " + req.getScheme() + "] " + "404");
    }

    private void respondWith500(OutputStream out, HttpRequest req) {
        PrintWriter writer = new PrintWriter(out, false);
        FileResponse fil = new FileResponse(ClassLoader.getSystemClassLoader().getResourceAsStream("500.html"), writer);
        respond(fil.toHttpResponse(Status._500_INTERNAL_ERROR, this.out));
        writer.close();
        logger.info("[" + req.getMethod() + " " + req.getPath() + " " + req.getScheme() + "] " + "500");
    }

    public void setMethodHandlers(HashMap<Method, Pair<String, Methods>> methodHandlers) {
        if (methodHandlers != null)
            this.methodHandlers = methodHandlers;
    }

    /**
     * Ignore the given path (for the given method access).
     * @param list list that contains
     */
    public void ignore(List<Pair<Methods, String>> list) {
        if (list == null)
            throw new IllegalArgumentException("Ignored paths list cannot be null");
        this.ignored = list;
    }


    /**
     * Precondition: @param method is not null.
     * @see #ignore(List)
     * @see #run()
     */
    private boolean isPathIgnored(Methods method, String path) {
        for (Pair<Methods, String> pair: ignored)
            if (pair.getFirst() == method && pair.getSecond().equals(path))
                return true;
        return false;
    }


    private List<HandlerTemplate> findHandlerTemplateListFromMethod(String method) {
        List<HandlerTemplate> returnTemplate = new ArrayList<>();
        for (HandlerTemplate handler : handlers) {
            if (handler.method.str.equals(method)) {
                returnTemplate.add(handler);
            }
        }
        return returnTemplate;
    }


    @Override
    public void close() throws IOException {
        this.client.close();
        this.in.close();
        this.out.close();
    }

    private void respond(String scheme, String status, byte[] body, PrintWriter stream, String name) {
        HttpResponseBuilder builder = new HttpResponseBuilder();
        HttpResponse res = builder
                .scheme(scheme)
                .code(Status.valueOf(Utility.enumStatusToString(status)).STATUS_CODE)
                .message(Status.valueOf(Utility.enumStatusToString(status)).MESSAGE)
                .body(body)
                .setStream(stream)
                .setHeader(Headers.DATE.NAME, ZonedDateTime.now().format(DateTimeFormatter.ofPattern(
                        "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                        ZoneId.of("GMT")
                        )
                ))
                .setHeader(Headers.SERVER.NAME, name)
                .setHeader(Headers.CONTENT_LANGUAGE.NAME, "en_US")
                .setHeader(Headers.CONTENT_LENGTH.NAME, ""+(body.length))
                .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
                .build();
        try {
            res.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void respond(HttpResponse res) {
        try {
            res.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setAllowForCustomMapping(boolean b) {
        this.allowForCustomMapping = b;
    }
}

/* Resources
! https://stackoverflow.com/questions/574594/how-can-i-create-an-executable-jar-with-dependencies-using-maven
! https://stackoverflow.com/questions/19035407/classloader-getresourceasstream-returns-null
! https://stackoverflow.com/questions/20389255/reading-a-resource-file-from-within-jar
! https://stackoverflow.com/questions/13482314/maven-how-to-include-src-main-resources
! https://stackoverflow.com/questions/5171957/access-file-in-jar-file
 */
