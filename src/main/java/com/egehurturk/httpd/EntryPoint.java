package com.egehurturk.httpd;


import com.egehurturk.handlers.FileResponse;
import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.handlers.JsonResponse;
import com.egehurturk.renderers.HTMLRenderer;
import com.egehurturk.util.ArgumentParser;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.MethodEnum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;




public class EntryPoint {

    /**
     * Note that configuration should not be as path, it only needs to be the name of the properties file
     */


    public static void main(String[] args) throws IOException {
        ArgumentParser parser = new ArgumentParser(args);
        HttpServer httpServer = parser.getHttpServer();
        HttpHandler handler = parser.getHandler();

        httpServer.allowCustomUrlMapping(true);
        httpServer.addHandler(MethodEnum.GET, "/hello", new MyHandler());
        httpServer.addHandler(MethodEnum.POST, "/*", handler);
        httpServer.addHandler(MethodEnum.GET, "/thismynewserver", new MyNewHandler());
        httpServer.addHandler(MethodEnum.GET, "/cemhurturk", new MyHandler());
        httpServer.addHandler(MethodEnum.GET, "/filehandling", new MyFileHandler());
        httpServer.addHandler(MethodEnum.GET, "/jsontest", new Json());
        httpServer.addHandler(MethodEnum.GET, "/paramtest", new Parameterized());
        httpServer.addHandler(MethodEnum.GET, "/template", new TemplateTest());
        httpServer.addHandler(MethodEnum.GET, "/soph", new Sophisticated());

        httpServer.start();
    }

    static class MyHandler implements Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(200)
                    .message("OK")
                    .body("<h1>Hello</h1>".getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+("<h1>Hello</h1>".length()))
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }

    static class MyFileHandler implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = null;
            try {
                FileResponse fil = new FileResponse("www/custom.html", response.getStream());
                res = fil.toHttpResponse();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return res;
        }
    }

    static class Parameterized implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            String body;
            if (request.getQueryParam("name").getFirst()) {
                body = request.getQueryParam("name").getSecond();
            } else {
                body = "<h3><i>Check logs (console)</i></h3>";
            }
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(200)
                    .message("OK")
                    .body(body.getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+(body.getBytes().length))
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }

    static class TemplateTest implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {

            HTMLRenderer contentRenderer = new HTMLRenderer("www/dist.html", response.getStream());

            contentRenderer.setVar("username", "monkey");
            contentRenderer.setVar("name", "Monkey man");
            contentRenderer.setVar("age", "23");
            contentRenderer.setVar("location", "Turkey");

            HTMLRenderer userRenderer = new HTMLRenderer("www/user.html", response.getStream());
            userRenderer.setVar("title", "User Profile");
            userRenderer.setVar("content", contentRenderer.render());
            return userRenderer.toHttpResponse();
        }
    }

    static class Sophisticated implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HTMLRenderer contentRenderer = new HTMLRenderer("www/dist.html", response.getStream());
            String username = request.getQueryParam("username").getFirst() ? request.getQueryParam("username").getSecond() : "null";
            String name = request.getQueryParam("name").getFirst() ? request.getQueryParam("name").getSecond() : "null";
            String age = request.getQueryParam("age").getFirst() ? request.getQueryParam("age").getSecond() : "null";
            String location = request.getQueryParam("location").getFirst() ? request.getQueryParam("location").getSecond() : "null";

            contentRenderer.setVar("username", username);
            contentRenderer.setVar("name", name);
            contentRenderer.setVar("age", age);
            contentRenderer.setVar("location", location);

            HTMLRenderer userRenderer = new HTMLRenderer("www/user.html", response.getStream());
            userRenderer.setVar("title", "User Profile");
            userRenderer.setVar("content", contentRenderer.render());
            return userRenderer.toHttpResponse();
        }
    }


    static class MyNewHandler implements Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(404)
                    .message("Not Found")
                    .body("<h1>Not Found 404 err</h1>".getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(HeaderEnum.CONTENT_LENGTH.NAME, ""+"<h1>Not Found 404 err</h1>".length())
                    .setHeader(HeaderEnum.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }

    static class Json implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            HttpResponse res = null;
            JsonResponse json = new JsonResponse(response.getStream(), request);
            json.validate(request);
            json.setBody("{\n" +
                    "    \"glossary\": {\n" +
                    "        \"title\": \"example glossary\",\n" +
                    "\t\t\"GlossDiv\": {\n" +
                    "            \"title\": \"S\",\n" +
                    "\t\t\t\"GlossList\": {\n" +
                    "                \"GlossEntry\": {\n" +
                    "                    \"ID\": \"SGML\",\n" +
                    "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
                    "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                    "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
                    "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
                    "\t\t\t\t\t\"GlossDef\": {\n" +
                    "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
                    "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
                    "                    },\n" +
                    "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n");
            res = json.toHttpResponse();
            return res;
        }
    }

}



/* FIXME: When `--congig <>` is passed as CLA, the server works. However, when all arguments are passed in as seperate
    fields, then the server closes because of `Port is already in use` */
// TODO: debug mode (server.properties)
// TODO: Take a look at logging directories, how you can log at different directories, etc.

