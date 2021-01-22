package com.egehurturk.handlers;

import com.egehurturk.exceptions.HttpRequestException;
import com.egehurturk.exceptions.MethodNotAllowedException;
import com.egehurturk.exceptions.NotFound404Exception;
import com.egehurturk.httpd.HttpRequest;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * Logging manager
     */
    protected Logger logger = LogManager.getLogger(HttpController.class);

    /**
     * List of handlers that are assigned to some method and path
     * Stored as {@link HandlerTemplate}
     */
    public List<HandlerTemplate> handlers;
    private boolean allowForCustomMapping;

    /**
     * Default constructor for this class.
     * @param socket                        - the client socket that server accepts. All
     *                                          input and output tasks are done with the socket's I/O streams.
     */
    public HttpController(Socket socket, List<HandlerTemplate> handlers) {
        this.client = socket;
        this.handlers = handlers;
    }

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
            System.out.println("[DEBUG][DEBUG] handlers associated with [" + req.getMethod() + "] --> " + Arrays.toString(methodTemplates.toArray()));
            System.out.println("[DEBUG][DEBUG] methodTemplates.isEmtpy() --> " + methodTemplates.isEmpty());
            if (methodTemplates.isEmpty()) {
                logger.error("No handler configurated for the path or method does not exists");
                throw new MethodNotAllowedException("Method is not allowed at path " + req.getPath(), 405, "Method Not Allowed");
            }

            // iterate over all handlers and find the handler template that is assigned to path
            for (HandlerTemplate templ: methodTemplates) {
                System.out.println("[DEBUG][DEBUG] current handler template in methodTemplate --> " + templ);
                // if exists
                System.out.println("[DEBUG][DEBUG] templ's path equals request's path --> " + templ.path.equals(req.getPath()));
                if (templ.path.equals(req.getPath())) {
                    System.out.println("[DEBUG][DEBUG] RESPONSE OBJECT BEING CREATED #before");
                    res = templ.handler.handle(req, res); // let handler to handle the request
                    System.out.println("[DEBUG][DEBUG] RESPONSE OBJECT BEING CREATED #after");
                    System.out.println("[DEBUG][DEBUG] sending response #before");
                    res.send();
                    System.out.println("[DEBUG][DEBUG] sending response #after");
                    foundHandler = true; // we found a handler
                    break;
                }
            }

            System.out.println("[DEBUG][DEBUG] foundHandler --> " + foundHandler);

            if (!foundHandler) {
                // check for default handler (all paths)
                for (HandlerTemplate template: methodTemplates) {
                    System.out.println("[DEBUG][DEBUG] current hadndler template looking for default --> " + template);
                    System.out.println("[DEBUG][DEBUG] template.path.equals(\"*/\") --> " + template.path.equals("/*"));
                    if (template.path.equals("/*")) {
                        System.out.println("[DEBUG][DEBUG] found default handler");
                        System.out.println("[DEBUG][DEBUG] prepareing response #before");
                        res = template.handler.handle(req, res);
                        System.out.println("[DEBUG][DEBUG] preparing response #after");
                        System.out.println("[DEBUG][DEBUG] sending response #before");
                        res.send();
                        System.out.println("[DEBUG][DEBUG] sending request #after");
                    } else {
                        // TODO: DONT THROW EXCEPTION
                        throw new NotFound404Exception("Handler is not found", 404, "Not Found");
                    }
                }
            }

        } catch (IOException | HttpRequestException e) {
            logger.error("Something's gone wrong...");
            // TODO: THROW 500 ERROR AT EVERY CATCH
            e.printStackTrace();
        } finally {
            try {
                // TODO: Server not closing?
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private int findHandlerTemplate(HandlerTemplate template) {
        return this.handlers.indexOf(template);
    }

    private List<HandlerTemplate> findHandlerTemplateListFromMethod(String method) {
        System.out.println("[DEBUG][DEBUG] findHandlerTemplateListFromMethod() called");
        List<HandlerTemplate> returnTemplate = new ArrayList<>();

        for (HandlerTemplate handler : handlers) {
            System.out.println("[DEBUG][DEBUG] current handler -->> " + handler);
            System.out.println("[DEBUG][DEBUG] handler.method.str.equals(method) -->> " + handler.method.str.equals(method));
            if (handler.method.str.equals(method)) {
                System.out.println("[DEBUG][DEBUG] template added to list");
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


    public void setAllowForCustomMapping(boolean b) {
        this.allowForCustomMapping = b;
    }


}
