package com.egehurturk.handlers;

import com.egehurturk.exceptions.FileSizeOverflowException;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.StatusEnum;
import com.egehurturk.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


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
     * {@link PrintWriter} necessary for {@link #toHttpResponse(String)}
     * method
     */
    private final PrintWriter writer;
    /**
     * Status necessary for {@link #toHttpResponse(String)}
     * method
     */
    private StatusEnum status;
    public final String BAD_REQ = "400.html";
    public final String INDEX = "index.html";
    public final String _404_NOT_FOUND = "404.html";
    public final String _NOT_IMPLEMENTED = "501.html";


    /**
     * Constructor that verifies path
     * @param path:                     request path, e.g. "/hello"
     * @param writer:                   Response writer
     */
    public FileResponseHandler(String path, PrintWriter writer) throws FileNotFoundException {
        this.path = path;
        this.writer = writer;
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
        File outputFile = prepareOutput();
        byte[] buffer = memoryAllocateForFile(outputFile);

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
                .code(this.status.STATUS_CODE)
                .message(this.status.MESSAGE)
                .body(buffer)
                .setStream(this.writer)
                .setHeader(HeaderEnum.DATE.NAME, dateHeader)
                .setHeader(HeaderEnum.SERVER.NAME, "Banzai")
                .setHeader(HeaderEnum.CONTENT_LANGUAGE.NAME, contentLang)
                .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+(buffer.length))
                .setHeader(HeaderEnum.CONTENT_TYPE.NAME, mimeType)
                .build();
        return response;
    }

    public HttpResponse toHttpResponse(StatusEnum status, PrintWriter writer) {
        File outputFile = prepareOutput();
        byte[] buffer = memoryAllocateForFile(outputFile);

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
        return builder
                .scheme("HTTP/1.1")
                .code(status.STATUS_CODE)
                .message(status.MESSAGE)
                .body(buffer)
                .setStream(writer)
                .setHeader(HeaderEnum.DATE.NAME, dateHeader)
                .setHeader(HeaderEnum.SERVER.NAME, "Banzai")
                .setHeader(HeaderEnum.CONTENT_LANGUAGE.NAME, contentLang)
                .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+(buffer.length))
                .setHeader(HeaderEnum.CONTENT_TYPE.NAME, mimeType)
                .build();
    }

    /**
     * Prepare the HTML file given GET request
     * @return                      - {@link File} output file
     */
    private File prepareOutput() {
        File outputFile;
        String resolvedFilePathUrl;
        // resolve the file and get the file that is stored in www/${resolvedFilePathUrl}
        outputFile = new File(this.path);
        // if the file does not exists throw 404
        if (!outputFile.exists()) {
            this.status = StatusEnum._404_NOT_FOUND;
            outputFile = new File("www", _404_NOT_FOUND);
        } else {
            if (outputFile.isDirectory()) {
                // /file -> index.html
                outputFile = new File(outputFile, INDEX);
            }
            if (outputFile.exists()) {
                this.status = StatusEnum._200_OK;
            } else {
                this.status = StatusEnum._404_NOT_FOUND;
                outputFile = new File("www", _404_NOT_FOUND);
            }
        }
        return outputFile;
    }

    private byte[] memoryAllocateForFile(File file) {
        byte[] bodyByte = null;
        try {
            bodyByte = Utility.readFile_IO(file);
        } catch (IOException | FileSizeOverflowException e) {
            this.logger.error("File size is too large");
            return null;
        }
        return bodyByte;
    }

}

