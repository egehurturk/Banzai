package com.egehurturk.handlers;

import com.egehurturk.exceptions.FileSizeOverflowException;
import com.egehurturk.httpd.HttpRequest;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.MethodEnum;
import com.egehurturk.util.StatusEnum;
import com.egehurturk.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;


public class HttpHandler implements Handler {

    /**
     * Bad request default file
     */
    public final String BAD_REQ = "400.html";
    /**
     * Index default file
     */
    public final String INDEX = "index.html";
    /**
     * 404 file
     */
    public final String _404_NOT_FOUND = "404.html";
    /*
        501 file
     */
    public final String _NOT_IMPLEMENTED = "501.html";
    /**
     * Fastest IO method that reads and stores file
     */
    public String FASTEST_IO = "readFile_IO";
    /**
     * Web Root that HTML files live in. This is a directory
     * that is the base URL. This directory will be considered and
     * used as a directory and {@link HttpHandler} will look
     * for the path in this directory
     *
     * E.g.
     *  root_dir
     *      |- www
     *          | - index.html
     *      |- src
     *          | main
     *          ...
     *  When a path is requested, e.g localhost:9090/index.html, {@link HttpHandler}
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
    private final String _strWebRoot;
    /**
     * Configuration
     */
    public final String WEBROOT_PROP = "server.webroot";
    public final String NAME_PROP = "server.name";
    /**
     * Status (e.g. 404, 500, 200)
     */
    public String status;

    /**
     * Configuration file
     */
    private Properties configuration = null;
    private String name;

    protected Logger logger = LogManager.getLogger(HttpHandler.class);

    /**
     * Base constructor
     * @param config                    - configuration file for accessing server name
     * @throws FileNotFoundException    - file not found
     */
    public HttpHandler(Properties config) throws FileNotFoundException {
        this.configuration = config;
        this._strWebRoot = config.getProperty(WEBROOT_PROP);
        if (!Utility.isDirectory(this._strWebRoot)) {
            logger.error("Web root is not a directory. Check if there exists a directory" +
                    "at root/www");
            throw new FileNotFoundException( "Web root directory not found. It should be" +
                    " placed in \"root/www\" where root" +
                    "is the top parent directory.");
        }
        this.webRoot = new File(this._strWebRoot);
    }

    public HttpHandler(String _strWebRoot, String name) throws FileNotFoundException {
        this._strWebRoot = _strWebRoot;
        if (!Utility.isDirectory(this._strWebRoot)) {
            logger.error("Web root is not a directory. Check if there exists a directory" +
                    "at root/www");
            throw new FileNotFoundException( "Web root directory not found. It should be" +
                    " placed in \"root/www\" where root" +
                    "is the top parent directory.");
        }
        this.webRoot = new File(_strWebRoot);
        this.name = name;
    }



    /**
     * Handle method overriding {@link Handler}
     * @param request                       - client HTTP request parsed into {@link HttpRequest}
     * @return response                     - response loaded into {@link HttpResponse}
     */
    @Override
    public HttpResponse handle(HttpRequest request, HttpResponse response) {
        HttpResponse res;
        switch (MethodEnum.valueOf(request.getMethod())) {
            case GET:
                res = handle_GET(request, response);
                break;
            case POST:
                res = handle_POST(request, response);
                break;
            default:
                res = handle_NOT_IMPLEMENTED(request, response);
        }
        return res;

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
     * Prepare the HTML file given request
     * @param req                   - {@link HttpRequest} object storing path
     * @return                      - {@link File} output file
     */
    private File prepareOutputWithMethod(HttpRequest req) {
        File outputFile = null;
        String resolvedFilePathUrl;
        if (req.getMethod().equals(MethodEnum.GET.str)) {
            // resolve the file and get the file that is stored in www/${resolvedFilePathUrl}
            resolvedFilePathUrl = resolvePath(req.getPath());
            outputFile = new File(this._strWebRoot, resolvedFilePathUrl);
            // if the file does not exists throw 404
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
        } else if (req.getMethod().equals(MethodEnum.POST.str)) {
        } else { // if request is neither GET nor POST
            this.status = StatusEnum._501_NOT_IMPLEMENTED.MESSAGE;
            outputFile = new File(this._strWebRoot, _NOT_IMPLEMENTED);
        }
        return outputFile;

    }

    /**
     * Handle get request
     * @param req           - {@link HttpRequest} request
     * @return              - {@link HttpResponse} response
     */
    public HttpResponse handle_GET(HttpRequest req, HttpResponse res) {
        String resolvedFilePathUrl;
        File outputFile = null;
        boolean statusReturned = false;
        // Host is a must for HTTP/1.1 servers
        if (!req.headers.containsKey(
                Utility.removeLastChars(HeaderEnum.HOST.NAME.trim().toLowerCase(), 1))
        ) {
            this.status = StatusEnum._400_BAD_REQUEST.MESSAGE;
            outputFile = new File(this._strWebRoot, BAD_REQ);
            statusReturned = true;
        }

        // bad request if path contains directory format
        if (!statusReturned) {
            if (req.getPath().contains("./") || req.getPath().contains("../")) {
                this.status = StatusEnum._400_BAD_REQUEST.MESSAGE;
                outputFile = new File(this._strWebRoot, BAD_REQ);
                statusReturned = true;
            }

            else if (req.getPath().equals("/")) {
                outputFile = prepareOutputWithMethod(req);
            }
            else if (Utility.isDirectory(req.getPath())){
                this.status = StatusEnum._400_BAD_REQUEST.MESSAGE;
                outputFile = new File(this._strWebRoot, BAD_REQ);
                statusReturned = true;
            }
            else {
                outputFile = prepareOutputWithMethod(req);
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

        String nameHeader = (this.configuration == null) ? this.name : this.configuration.getProperty(NAME_PROP);
        HttpResponseBuilder builder = new HttpResponseBuilder();
        HttpResponse response = builder
                .scheme("HTTP/1.1")
                .code(StatusEnum.valueOf(Utility.enumStatusToString(status)).STATUS_CODE)
                .message(StatusEnum.valueOf(Utility.enumStatusToString(status)).MESSAGE)
                .body(bodyByte)
                .setStream(new PrintWriter(res.getStream(), false))
                .setHeader(HeaderEnum.DATE.NAME, dateHeader)
                .setHeader(HeaderEnum.SERVER.NAME, nameHeader)
                .setHeader(HeaderEnum.CONTENT_LANGUAGE.NAME, contentLang)
                .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+(bodyByte.length))
                .setHeader(HeaderEnum.CONTENT_TYPE.NAME, mimeType)
                .build();
        return response;
    }

    public HttpResponse handle_POST(HttpRequest req, HttpResponse res) {
        return new HttpResponse();
    }

    public HttpResponse handle_NOT_IMPLEMENTED(HttpRequest req, HttpResponse res) {
        return new HttpResponse();
    }
}