package com.egehurturk.httpd;


import com.egehurturk.annotations.BanzaiHandler;
import com.egehurturk.annotations.HandlerMethod;
import com.egehurturk.exceptions.MalformedHandlerException;
import com.egehurturk.handlers.FileResponse;
import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.JsonResponse;
import com.egehurturk.renderers.HTMLRenderer;
import com.egehurturk.util.ArgumentParser;
import com.egehurturk.util.Headers;
import com.egehurturk.util.Methods;

import java.io.PrintWriter;


class EntryPoint {

    // SET THIS TO TRUE
    public static final boolean PRODUCTION_ENV = true;

    /**
     * Note that configuration should not be as path, it only needs to be the name of the properties file
     */


    public static void main(String[] args) throws MalformedHandlerException {
        ArgumentParser parser = new ArgumentParser(args);
        HttpServer httpServer = parser.getHttpServer();


        if (!PRODUCTION_ENV) {
            httpServer.addHandler(MHandler.class);
            httpServer.addHandler(MaHandler.class);
            httpServer.allowCustomUrlMapping(true);
            httpServer.addHandler(Methods.GET , "/hello"           , new MyHandler());
//            httpServer.addHandler(Methods.GET , "/thismynewserver" , new MyNewHandler());
//            httpServer.addHandler(Methods.GET , "/cemhurturk"      , new MyHandler());
//            httpServer.addHandler(Methods.GET , "/filehandling"    , new MyFileHandler());
//            httpServer.addHandler(Methods.GET , "/jsontest"        , new Json());
//            httpServer.addHandler(Methods.GET , "/paramtest"       , new Parameterized());
//            httpServer.addHandler(Methods.GET , "/template"        , new TemplateTest());
//            httpServer.addHandler(Methods.GET , "/soph"            , new Sophisticated());
//            httpServer.ignore(Methods.GET, "/jsontest");
//            httpServer.ignore(Methods.GET, "/cemhurturk");
//            httpServer.ignore(Methods.GET, "/asdfjasdlfkjals;kdfjadkls;");
//            httpServer.ignore(Methods.GET, "kdfjadkls;");
//            httpServer.ignore(Methods.GET, "/thismynewserver");
//            httpServer.ignore(Methods.POST, "sdfl;kgjsd;lkgjsfl;k");
//            httpServer.ignore(Methods.POST, null);
//            httpServer.ignore(null, null);
//            httpServer.ignore(null, "/adsdassad");
        }
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
                    .setHeader(Headers.CONTENT_LENGTH.NAME, ""+("<h1>Hello</h1>".length()))
                    .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
                    .build();
            return res;
        }
    }

    static class MyFileHandler implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            FileResponse fil = new FileResponse("www/custom.html", response.getStream());
            // FIXME: fil is null
            HttpResponse res = fil.toHttpResponse();
            System.out.println("Debug from file response, file response content: " + fil.asString());
            return res;
        }
    }

    static class Parameterized implements com.egehurturk.handlers.Handler {
        @Override
        public HttpResponse handle(HttpRequest request, HttpResponse response) {
            String body;
            if (request.hasParameter("name")) {
                body = request.getParameter("name");
            } else {
                body = "<h3><i>Check logs (console)</i></h3>";
            }
            HttpResponse res = new HttpResponseBuilder().scheme("HTTP/1.1")
                    .code(200)
                    .message("OK")
                    .body(body.getBytes())
                    .setStream(new PrintWriter(response.getStream(), false))
                    .setHeader(Headers.CONTENT_LENGTH.NAME, ""+(body.getBytes().length))
                    .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
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
            String username = request.getParameter("username");
            String name = request.getParameter("name");
            String age = request.getParameter("age");
            String location = request.getParameter("location");

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
                    .setHeader(Headers.CONTENT_LENGTH.NAME, ""+"<h1>Not Found 404 err</h1>".length())
                    .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
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

@BanzaiHandler
class MHandler {
    @HandlerMethod(path = "/metallica")
    public static HttpResponse handle_metallica(HttpRequest req, HttpResponse res) {
        return new HttpResponseBuilder().scheme("HTTP/1.1").code(200).message("OK")
            .body("<h1>Metallica</h1>".getBytes())
            .setStream(new PrintWriter(res.getStream(), false))
            .setHeader(Headers.CONTENT_LENGTH.NAME, ""+("<h1>Metallica</h1>".length()))
            .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
            .build();
    }

    @HandlerMethod(path = "/jimi_hendrix")
    public static HttpResponse handle_jimi(HttpRequest req, HttpResponse res) {
        return new HttpResponseBuilder().scheme("HTTP/1.1").code(200).message("OK")
            .body("<h1>Jimi Hendrix</h1>".getBytes())
            .setStream(new PrintWriter(res.getStream(), false))
            .setHeader(Headers.CONTENT_LENGTH.NAME, ""+("<h1>Jimi Hendrix</h1>".length()))
            .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
            .build();
    }

    @HandlerMethod(path = "/should_return_err")
    static HttpResponse should_Return_err(HttpRequest req, HttpResponse res) {
        return null;
    }

    @HandlerMethod(path = "/testing_methods")
    private static HttpResponse return_suc(HttpRequest req, HttpResponse res) {
        return new HttpResponse();
    }
}

@BanzaiHandler
class MaHandler {
    @HandlerMethod(path = "/you")
    public static HttpResponse handle_metallica(HttpRequest req, HttpResponse res) {
        return new HttpResponseBuilder().scheme("HTTP/1.1").code(200).message("OK")
            .body("<h1>You</h1>".getBytes())
            .setStream(new PrintWriter(res.getStream(), false))
            .setHeader(Headers.CONTENT_LENGTH.NAME, ""+("<h1>You</h1>".length()))
            .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
            .build();
    }

    @HandlerMethod(path = "/are")
    public static HttpResponse handle_jimi(HttpRequest req, HttpResponse res) {
        return new HttpResponseBuilder().scheme("HTTP/1.1").code(200).message("OK")
            .body("<h1>Are</h1>".getBytes())
            .setStream(new PrintWriter(res.getStream(), false))
            .setHeader(Headers.CONTENT_LENGTH.NAME, ""+("<h1>Are/h1>".length()))
            .setHeader(Headers.CONTENT_TYPE.NAME, "text/html")
            .build();
    }


}
