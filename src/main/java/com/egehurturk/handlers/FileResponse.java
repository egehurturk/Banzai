package com.egehurturk.handlers;

import com.egehurturk.exceptions.FileSizeOverflowException;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.Pair;
import com.egehurturk.util.Status;
import com.egehurturk.util.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
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
public class FileResponse implements ResponseType {

    protected Logger logger = LogManager.getLogger(FileResponse.class);
    /**
     * Path representing output file path
     * Everything will be based on this
     */
    private String path;
    /**
     * {@link PrintWriter} necessary for {@link #toHttpResponse()}
     * method
     */
    private final PrintWriter writer;

    /**
     * Required for JAR file
     */
    private InputStream filestream;
    /**
     * Status necessary for {@link #toHttpResponse()}
     * method
     */
    private Status status;
    public final String INDEX            = "index.html";
    public final String _404_NOT_FOUND   = "404.html";
    public String webroot                = "www";
    private byte[] body;



    /**
     * Constructor that verifies path
     * @param path request path, e.g. "/hello"
     * @param writer Response writer
     */
    public FileResponse(String path, PrintWriter writer) {
        this.path = path;
        this.writer = writer;
    }

    /**
     * Constructor with input stream and print writer.
     * Classes that read files from stream, i.e. using {@link ClassLoader#getResourceAsStream(String)}, uses this
     * constructor and passes the stream as a parameter.
     *
     * <p>This class then buffers the stream and converts the contents to HttpResponse
     *
     * @param filestream input stream
     * @param writer HttpResponse print writer
     */
    public FileResponse(InputStream filestream, PrintWriter writer) {
        this.filestream = filestream;
        this.writer = writer;
    }

    // Getters
    public String getWebroot() {
        return webroot;
    }

    // Setters
    public void setWebroot(String webroot) {
        this.webroot = webroot;
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
    @Override
    public HttpResponse toHttpResponse() {

        byte[] buffer;
        String mimeType = "text/html";
        if (this.filestream == null) { // * means that the class is called with a path to file
            Pair<String, byte[]> pair = prepareOutput();
            buffer = pair.getSecond();
            mimeType = pair.getFirst();
        } else {
            buffer = inputStreamToBuffer(filestream);
        }
        this.body = buffer;


        ZonedDateTime now = ZonedDateTime.now();
        String dateHeader = now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                ZoneId.of("GMT")
                )
        );
        String contentLang = "en_US";

        return new HttpResponseBuilder().factory("HTTP/1.1", this.status.STATUS_CODE, this.status.MESSAGE, buffer, this.writer,
                mimeType, dateHeader, "Banzai", contentLang, buffer.length
        );
    }

    /**
     * Convert this to {@link HttpResponse}.
     * @param status status to be sent in the response
     * @param writer writer associated with the response
     * @return response
     */
    public HttpResponse toHttpResponse(Status status, PrintWriter writer) {

        byte[] buffer;
        String mimeType = "text/html";
        if (this.filestream == null) {
            Pair<String, byte[]> pair = prepareOutput();
            buffer = pair.getSecond();
            mimeType = pair.getFirst();
        } else {
            buffer = inputStreamToBuffer(filestream);
        }

        ZonedDateTime now = ZonedDateTime.now();
        String dateHeader = now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                ZoneId.of("GMT")
                )
        );
        String contentLang = "en_US";

        return new HttpResponseBuilder().factory("HTTP/1.1", status.STATUS_CODE, status.MESSAGE, buffer, writer,
                mimeType, dateHeader, "Banzai", contentLang, buffer.length
        );
    }

    /**
     * Returns file/stream contents as string.
     *
     * Body must be set (using {@link #toHttpResponse()} and then this method should be used.
     * @return String
     */
    public String asString() {
        if (this.body != null)
            return new String(this.body);
        logger.warn("Body is null");
        return null;
    }

    /**
     * Prepare the HTML file given path to file
     * @return                      - {@link File} output file
     */
    private Pair<String, byte[]> prepareOutput() {
        File outputFile;
        String mime = "text/html";
        byte[] buffer;
        outputFile = new File(this.path);
        if (!outputFile.exists()) {
            this.status = Status._404_NOT_FOUND;
            buffer = inputStreamToBuffer(ClassLoader.getSystemClassLoader().getResourceAsStream(_404_NOT_FOUND));
        } else {
            if (outputFile.isDirectory()) {
                outputFile = new File(outputFile, INDEX);
            }
            if (outputFile.exists()) {
                this.status = Status._200_OK;
                try {
                    mime = Files.probeContentType(outputFile.toPath());
                } catch (IOException e) {
                    logger.error("Cannot determine mime type. Using text/html as default");
                }
                buffer = memoryAllocateForFile(outputFile);
            } else {
                this.status = Status._404_NOT_FOUND;
                buffer = inputStreamToBuffer(ClassLoader.getSystemClassLoader().getResourceAsStream(_404_NOT_FOUND));
            }
        }
        return new Pair<>(mime, buffer);
    }


    /**
     * Store the File in memory with byte array
     * @param file file to be stored in
     * @return buffered File
     */
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

    /**
     * Convert input stream to byte array
     *
     * @param is input stream
     * @return buffered input stream
     */
    private byte[] inputStreamToBuffer(InputStream is) {
        ByteArrayOutputStream _buf = new ByteArrayOutputStream();
        byte[] data = new byte[16384];
        int nRead;
        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                _buf.write(data,0,nRead);
            }
        } catch (IOException err) {
            logger.error("Cannot convert input stream to buffer (byte array). ");
        }
        byte[] array = _buf.toByteArray();
        try {
            _buf.close();
        } catch (IOException e) {
            logger.warn("Cannot close byte buffer. [silenced] ");
        }
        return array;
    }

}


