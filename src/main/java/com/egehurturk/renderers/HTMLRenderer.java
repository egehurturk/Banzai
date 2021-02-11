package com.egehurturk.renderers;

import com.egehurturk.handlers.ResponseType;
import com.egehurturk.httpd.HttpResponse;
import com.egehurturk.httpd.HttpResponseBuilder;
import com.egehurturk.util.StatusEnum;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

/**
 * E.g.
 * <code>
 *     <h1>[@username] profile</h1>
 *      <img src="[@photoURL]" class="photo" alt="[@name]" />
 *      <b>Name:</b> [@name]<br />
 *      <b>Age:</b> [@age]<br />
 *      <b>Location:</b> [@location]<br />
 *      </code>
 */
public class HTMLRenderer implements ResponseType {
    // HTML file
    private final String htmlPath;
    private StatusEnum status;
    private final PrintWriter writer;
    public final String BAD_REQ = "400.html";
    public final String INDEX = "index.html";
    public final String _404_NOT_FOUND = "404.html";
    public final String _NOT_IMPLEMENTED = "501.html";
    // Variables (Flask style)
    private HashMap<String, String> vars = new HashMap<String, String>();

    /**
     * Basic Constructor
     * @param htmlPath: html file path to be rendered
     */
    public HTMLRenderer(String htmlPath, PrintWriter writer) {
        this.htmlPath = htmlPath;
        this.writer = writer;
    }

    private File prepareOutput() {
        File outputFile;
        outputFile = new File(this.htmlPath);
        // if the file does not exists throw 404
        if (!outputFile.exists()) {
            this.status = StatusEnum._404_NOT_FOUND;
            outputFile = new File("www", _404_NOT_FOUND);
        } else {
            this.status = StatusEnum._200_OK;
        }
        return outputFile;
    }

    public String render() {
        Path outputPath = prepareOutput().toPath();
        String html = read(outputPath);

        for (Map.Entry<String, String> entry : this.vars.entrySet()) {
            String tagToReplace = "[@" + entry.getKey() + "]";
            html = html.replace(tagToReplace, entry.getValue());
        }
        return html;
    }

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


    public String getHtmlPath() {
        return htmlPath;
    }
    public String getVar(String varArg) {
        return vars.get(varArg);
    }
    public void setVar(String varArgInHtml, String varArg) {
        this.vars.put(varArgInHtml, varArg);
    }

}


/*

<!DOCTYPE html>
<html>
<body>
<h1>My First Heading</h1>

<p>My first paragraph.</p>

</body>
</html>


 */