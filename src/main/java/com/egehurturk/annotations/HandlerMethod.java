package com.egehurturk.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the method as a {@link com.egehurturk.handlers.Handler} that regulates the HTTP
 * request-response flow. Any method that is marked with this annotation is considered to
 * be a {@code Handler}.
 * <p>
 * Each method which is annotated by this annotation is similar to a class that implements
 * {@code Handler}. The former includes a method called {@code handle} that takes in
 * {@link com.egehurturk.httpd.HttpRequest} and {@link com.egehurturk.httpd.HttpResponse} and
 * returns {@code HttpResponse}. Annotating a method with this annotation is similar
 * to the former approach - except, one does not need to create a class.
 * <p>
 * This annotation annotated methods must be in a class which is annotated by {@link BanzaiHandler}.
 * This is a must since handler methods should be grouped together and that annotation makes this possible.
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
 * @see BanzaiHandler
 * @version 1.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandlerMethod {
}
