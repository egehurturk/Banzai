package com.egehurturk.handlers;

import com.egehurturk.httpd.HttpRequest;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.Json;
import com.egehurturk.util.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Encapsulates everything concerning JSON response
 */
public class JsonResponse implements ResponseType {
    protected Logger logger = LogManager.getLogger(FileResponse.class);

    /**
     * {@link PrintWriter} necessary for {@link #toHttpResponse()}
     * method
     */
    private final PrintWriter writer;
    private String body;
    private Boolean valid;

    /**
     * Constructor that verifies path
     * @param writer Response writer
     * @param body Json body
     */
    public JsonResponse(PrintWriter writer, String body) {
        this.writer = writer;
        this.body   = body;
    }

    public JsonResponse(PrintWriter writer) {
        this.writer = writer;
    }

    public JsonResponse(PrintWriter writer, HttpRequest request) {
        this.writer = writer;
        validate(request);
    }


    public void validate(HttpRequest req) {
        // * Note for future documentation: request headers are stored in lowercase and trimmed

        if (!req.hasHeader("Accept".toLowerCase())) {
            this.valid = false;
            return;
        }
        String accept = req.getHeader("Accept".toLowerCase());
        this.valid = accept.contains("application/json") || accept.contains("*/*");
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isValid() {
        return valid;
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
     *              JsonResponseHandler json = new JsonResponseHandler(response.getStream());
     *              json.setBody("{"name": "hello"}");
     *              return json.toHttpResponse();
     *          }
     *      }
     *
     * </code>
     *
     * @return Http response object ready to being send in {@link Handler} interfaces
     */
    @Override
    public HttpResponse toHttpResponse() {

        if (this.valid == null) {
            throw new IllegalStateException("HTTP Request is required to check if the client accepts JSON as a return type. You can pass" +
                    " the HTTP request {HttpRequest} object to this class' constructor or call the method `validate(HttpRequest)`");
        }

        ZonedDateTime now = ZonedDateTime.now();
        String dateHeader = now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                ZoneId.of("GMT")
                )
        );
        String contentLang = "en_US";
        String mimeType    = "application/json";
        Status status  = Status._200_OK;

        if (!this.valid) {
            status = Status._406_NOT_ACCEPTABLE;
            FileResponse file = new FileResponse(ClassLoader.getSystemClassLoader().getResourceAsStream("406.html"), this.writer);
            return file.toHttpResponse(status, this.writer);
        }
        if (this.body == null) {
            logger.error("Body of JSON request is empty. Server automatically created JSON body.");
            this.body = Json.prettyPrintJSON("{\"Server Response\": {\"title\": \"Null Body\", \"body\": \"Body of JSON request is not set (This message is autogenerated by Banzai. Check logs from console)\"}}");
        }

        return new HttpResponseBuilder()
                .factory("HTTP/1.1", status.STATUS_CODE, status.MESSAGE,
                        this.getBody().getBytes(), this.writer,
                        mimeType, dateHeader, "Banzai",
                        contentLang, this.getBody().getBytes().length
                );
    }


}
