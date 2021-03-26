package com.egehurturk.renderers;

import com.egehurturk.handlers.ResponseType;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.Pair;
import com.egehurturk.util.Status;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/* Example HTML file
 * E.g.
 * <code>
 *     <h1>[@username] profile</h1>
 *      <img src="[@photoURL]" class="photo" alt="[@name]" />
 *      <b>Name:</b> [@name]<br />
 *      <b>Age:</b> [@age]<br />
 *      <b>Location:</b> [@location]<br />
 *      </code>
 */

/**
 * This class parses HTML containing tags and replaces tags ([@ ... ]) with
 * values taken from user
 *
 * @author egehurturk
 * @version 1.0
 */
public class HTMLRenderer implements ResponseType {
    // HTML file
    private final String htmlPath;
    private Status status;
    private final PrintWriter writer;
    public final String BAD_REQ = "400.html";
    public final String INDEX = "index.html";
    public final String _404_NOT_FOUND = "404.html";
    public final String _NOT_IMPLEMENTED = "501.html";
    // Variables (Flask style)
    private HashMap<String, String> vars = new HashMap<String, String>();

    /**
     * All args Constructor
     * @param htmlPath: html file path to be rendered
     */
    public HTMLRenderer(String htmlPath, PrintWriter writer) {
        this.htmlPath = htmlPath;
        this.writer = writer;
    }

    /**
     * Return a {@link Pair} with input stream and a file
     * Either file or input stream (first, second) is going to be null
     *
     * @return pair of input stream and file
     */
    private Pair<File, InputStream> prepareOutput() {
        File outputFile = new File(this.htmlPath);
        InputStream stream = null;
        if (!outputFile.exists()) {
            this.status = Status._404_NOT_FOUND;
            stream = ClassLoader.getSystemClassLoader().getResourceAsStream(_404_NOT_FOUND);
        } else {
            this.status = Status._200_OK;
        }
        return new Pair<>(outputFile, stream);
    }

    /**
     * Renders HTML document, replacing tags ([@ ... ])
     * with variables set by user
     *
     * @return string html
     */
    public String render() {
        File outputfile;
        InputStream stream;
        String html;
        Pair<File, InputStream> pair = prepareOutput();
        if (pair.getSecond() != null) {
            stream = pair.getSecond();
            html = new String(inputStreamToBuffer(stream));
        } else {
            outputfile = pair.getFirst();
            Path outputPath = outputfile.toPath();
            html = read(outputPath);

            for (Map.Entry<String, String> entry : this.vars.entrySet()) {
                String tagToReplace = "[@" + entry.getKey() + "]";
                html = html.replace(tagToReplace, entry.getValue());
            }
        }
        return html;
    }

    /**
     * Read a path, return it as string
     * @param path path of file
     * @return path stored in string
     */
    private static String read(Path path) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( path, StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    /**
     * Converts this to {@link HttpResponse}
     *
     * @return response object
     */
    @Override
    public HttpResponse toHttpResponse() {
        ZonedDateTime now = ZonedDateTime.now();
        String dateHeader = now.format(DateTimeFormatter.ofPattern(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
                ZoneId.of("GMT")
                )
        );
        String contentLang = "en_US", mimeType = "text/html", body = this.render();
        return new HttpResponseBuilder().factory("HTTP/1.1", this.status.STATUS_CODE, this.status.MESSAGE, body.getBytes(), this.writer,
                mimeType, dateHeader, "Banzai", contentLang, body.getBytes().length
        );
    }

    // Mutators
    public String getHtmlPath() {
        return htmlPath;
    }
    public String getVar(String varArg) {
        return vars.get(varArg);
    }

    // Accessors
    public void setVar(String varArgInHtml, String varArg) {
        this.vars.put(varArgInHtml, varArg);
    }

    /**
     * Read input stream and store in memory
     * @param is input stream
     * @return input stream stored in memory
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
            System.err.println("Cannot convert input stream to buffer (byte array). ");
        }

        return _buf.toByteArray();
    }

}
