package com.egehurturk.annotations_t;

import com.egehurturk.annotations.BanzaiHandler;
import com.egehurturk.annotations.HandlerMethod;
import com.egehurturk.handlers.FileResponse;
import com.egehurturk.httpd.HttpRequest;
import com.egehurturk.httpd.HttpResponse;

@BanzaiHandler
public class Mhandler {

    @HandlerMethod(path = "/jimi_hendrix")
    public static HttpResponse jimi_hendrix(HttpRequest req, HttpResponse res) {
        FileResponse fil = new FileResponse("jimi.html", res.getStream());
        log("It is an experience.");
        return fil.toHttpResponse();
    }

    @HandlerMethod(path = "/metallica")
    static HttpResponse metallica(HttpRequest req, HttpResponse res) {
        FileResponse fil = new FileResponse("metallica.html", res.getStream());
        log("Metallica BRINGS METAL TO MOSCOW!");
        return fil.toHttpResponse();
    }

    @HandlerMethod(path = "/beatles")
    private static HttpResponse beatles(HttpRequest req, HttpResponse res) {
        FileResponse fil = new FileResponse("beatles.html", res.getStream());
        log("Blackbird singing in the dead of nighhht");
        return fil.toHttpResponse();
    }

    private static void log(String message) {
        System.out.println("[    main] - INFO - " + message);
    }

}
