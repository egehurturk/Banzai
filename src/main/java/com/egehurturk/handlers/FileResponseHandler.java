package com.egehurturk.handlers;

import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.util.StatusEnum;
import com.egehurturk.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;


/**
 * Encapsulates everything concerning file handling and/or HTML file
 * returning. This class is a result of my approaches to beautify and
 * improve the design & implementation of http response protocol
 */
public class FileResponseHandler {

    protected Logger logger = LogManager.getLogger(FileResponseHandler.class);
    /**
     * Path representing output file path
     * Everything will be based on this
     */
    private final String path;
    /**
     * {@link PrintWriter} necessary for {@link #toHttpResponse()}
     * method
     */
    private final PrintWriter writer;
    /**
     * Status necessary for {@link #toHttpResponse()}
     * method
     */
    private StatusEnum status;
    public final String BAD_REQ = "400.html";
    public final String INDEX = "index.html";
    public final String _404_NOT_FOUND = "404.html";
    public final String _NOT_IMPLEMENTED = "501.html";
    public String webroot;


    /**
     * Constructor that verifies path
     * @param path:                     request path, e.g. "/hello"
     * @param writer:                   Response writer
     * @param webRoot:                  Web root where HTML files live in, e.g. "www"
     */
    public FileResponseHandler(String path, PrintWriter writer, String webRoot) throws FileNotFoundException {
        this.path = path;
        this.writer = writer;
        if (!Utility.isDirectory(webRoot)) {
            logger.error("Web root is not a directory. Check if there exists a directory" +
                    "at root/www");
            // TODO: return
            return;

        }
        this.webroot = webRoot;
    }



    /**
     * Converts File output to {@link HttpResponse} response
     * for future use in classes that implemenets {@link Handler} interface
     *
     * <p>Sample usage is explained below:
     *
     * <code>
     *
     *     class MyHandler implements Handler {
     *          @Override
     *          public HttpResponse handle(HttpRequest request, HttpResponse response) {
     *              FileResponseHandler file = new FileResponseHandler("www/index.html", response.getStream(), "www");
     *              return file.toHttpResponse();
     *          }
     *      }
     *
     * </code>
     *
     * @return Http response object ready to being send in {@link Handler} interfaces
     */
    public HttpResponse toHttpResponse() {
        File outputFile= prepareOutputWithMethod();
        return null;
    }

    /**
     * Resolves path, i.e translates URL to local system path
     * @param reqPath           - requested path
     * @return resolved path    - resolved path
     */
    private String resolvePath(String reqPath) {
        Path resolved = FileSystems.getDefault().getPath("");
        Path other = FileSystems.getDefault().getPath(reqPath);
        for (Path path: other) {
            // security
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

    /**
     * Prepare the HTML file given GET request
     * @return                      - {@link File} output file
     */
    private File prepareOutputWithMethod() {
        File outputFile = null;
        String resolvedFilePathUrl;
        // resolve the file and get the file that is stored in www/${resolvedFilePathUrl}
        resolvedFilePathUrl = resolvePath(this.path);
        outputFile = new File(this.webroot, resolvedFilePathUrl);
        // if the file does not exists throw 404
        if (!outputFile.exists()) {
            this.status = StatusEnum._404_NOT_FOUND;
            outputFile = new File(this.webroot, _404_NOT_FOUND);
        } else {
            if (outputFile.isDirectory()) {
                // /file -> index.html
                outputFile = new File(outputFile, INDEX);
            }
            if (outputFile.exists()) {
                this.status = StatusEnum._200_OK;
            } else {
                this.status = StatusEnum._404_NOT_FOUND;
                outputFile = new File(this.webroot, _404_NOT_FOUND);
            }
        }
        return outputFile;
    }



}





// scheme
// code
// body
// message
// stream
// date
// server
//