package com.egehurturk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the class as a handler container that takes part in the HTTP request-response flow.
 * Any class that is marked with this annotation is considered to be a handler container, that
 * is, containing methods with {@link HandlerMethod} annotated-methods.
 * <p>
 * In contrast to the interface-based {@link com.egehurturk.handlers.Handler} approach,
 * this annotation groups methods. Instead of creating a class that implements {@code Handler}
 * for every path, this approach groups different methods and each method that is
 * annotated with {@code HandlerMethod} acts like a class that implements {@code Handler}.
 * <p>
 * The client registers a class that is annotated with this annotation with
 * {@link com.egehurturk.httpd.HttpServer#addHandler(Class)}.
 * <p>
 * Example:
 * {@code
 *      @BanzaiHandler
 *      public class MyHandler {
 *          @HandlerMethod
 *          public HttpResponse handle_JIMI(HttpRequest req, HttpResponse res) {
 *              HttpResponse resp = // ...
 *              // ...
 *              return resp;
 *          }
 *
 *          @HandlerMethod
 *          public HttpResponse handle_METALLICA(HttpRequest req, HttpResponse res) {
 *              HttpResponse resp = // ...
 *              // ...
 *              return resp;
 *          }
 *      }
 *
 *      // client's entry point class
 *      HttpServer s = // ...;
 *      s.addHandler(MyHandler.class);
 * }
 * <p>
 *
 * @see HandlerMethod
 * @version 1.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BanzaiHandler {
}
