### Table of Contents
- [`com.egehurturk.httpd`](#comegehurturkhttpd)
  - [`com.egehurturk.httpd.HttpServer`](#comegehurturkhttpdhttpserver)
      - [Constructor Summary](#constructor-summary)
      - [Important Methods](#important-methods)
  - [`com.egehurturk.httpd.HttpRequest`](#comegehurturkhttpdhttprequest)
    - [Anatomy of HTTP Requests](#anatomy-of-http-requests)
    - [Constructors](#constructors)
    - [Important Methods](#important-methods-1)
    - [Example Usage:](#example-usage)
  - [`com.egehurturk.httpd.HttpResponse`](#comegehurturkhttpdhttpresponse)
    - [Anatomy of HTTP Responses](#anatomy-of-http-responses)
    - [Factory Methods](#factory-methods)
    - [Important Methods](#important-methods-2)
  - [`com.egehurturk.httpd.HttpResponseBuilder`](#comegehurturkhttpdhttpresponsebuilder)
    - [Example Usage](#example-usage-1)
  - [`com.egehurturk.httpd.EntryPoint`](#comegehurturkhttpdentrypoint)
- [`com.egehurturk.annotations`](#comegehurturkannotations)
  - [`com.egehurturk.annotations.BanzaiHandler`](#comegehurturkannotationsbanzaihandler)
  - [`com.egehurturk.annotations.HandlerMethod`](#comegehurturkannotationshandlermethod)
- [`com.egehurturk.handlers`](#comegehurturkhandlers)
  - [`com.egehurturk.handlers.ResponseType`](#comegehurturkhandlersresponsetype)
    - [Important Methods](#important-methods-3)
  - [`com.egehurturk.handlers.FileResponse`](#comegehurturkhandlersfileresponse)
    - [Constructors](#constructors-1)
    - [Examples](#examples)
  - [`com.egehurturk.handlers.JsonResponse`](#comegehurturkhandlersjsonresponse)
    - [Constructors](#constructors-2)
    - [Important Methods](#important-methods-4)
    - [Examples:](#examples-1)
  - [`com.egehurturk.handlers.Handler`](#comegehurturkhandlershandler)
- [`com.egehurturk.util`](#comegehurturkutil)
  - [`com.egehurturk.util.ArgumentParser`](#comegehurturkutilargumentparser)
  - [`com.egehurturk.util.HeaderStatus`](#comegehurturkutilheaderstatus)
  - [`com.egehurturk.util.Headers`](#comegehurturkutilheaders)
      - [Constructor Summary](#constructor-summary-1)
      - [Fields](#fields)
  - [`com.egehurturk.util.Methods`](#comegehurturkutilmethods)
- [`com.egehurturk.renderers`](#comegehurturkrenderers)
  - [`com.egehurturk.handlers.HTMLRenderer`](#comegehurturkrenderershtmlrenderer)
    - [Constructors](#constructors-3)
    - [Important Methods](#important-methods-5)
    - [Examples:](#examples-2)
- [`com.egehurturk.exceptions`](#comegehurturkexceptions)
  - [`com.egehurturk.exceptions.ConfigurationException`](#comegehurturkexceptionsconfigurationexception)
  - [`com.egehurturk.exceptions.FileSizeOverflowException`](#comegehurturkexceptionsfilesizeoverflowexception)
  - [`com.egehurturk.exceptions.HttpRequestException`](#comegehurturkexceptionshttprequestexception)
  - [`com.egehurturk.exceptions.HttpResponseException`](#comegehurturkexceptionshttpresponseexception)
  - [`com.egehurturk.exceptions.HttpVersionNotSupportedException`](#comegehurturkexceptionshttpversionnotsupportedexception)

# `com.egehurturk.httpd`
This package is about how Banzai handles HTTP. [HTTP](Http.md) provides a detailed explanation about the process.

## `com.egehurturk.httpd.HttpServer`
This class acts as a HTTP server for providing HTTP connection. Uses the TCP protocol as the transport layer defined in OSI (OSI 4th layer) to communicate with the client. This class extends [`com.egehurturk.core.BaseServer`](#comegehurturkcorebaseserver).

This class is configured by setting:
* Server port
* Server host
* Server name
* Server webroot
* Debug mode

fields in a `server.properties` configuration file. The configuration process happens in [`com.egehurturk.core.BaseServer`](#comegehurturkcorebaseserver). This class overrides the `configureServer` method of `BaseServer`.

#### Constructor Summary
`public HttpServer()`:
* The constructor initializes all objects to avoid `NullPointerException`.
* Other than that, the constructor does nothing.
* The purpose of this constructor is to configure `HttpServer` via a `server.properties` file.
* This is the recommended way to instantiate this class.

`public HttpServer(String)`:
* Accepts the configuration file (`server.properties`) path.
* The constructor then sets the configuration file path as the given value, and calls the `configureServer` method to configure the server.

#### Important Methods

`allowCustomUrlMapping(boolean)`:
* This method enables the server to allow for custom URL mappings.
  * In other words, certain paths can be mapped to certain Handlers.
  * For example, the path `/hello` may be mapped to `MyHandler` class that implements the `Handler` interface
* Parameters:
  * `boolean allow`: whether it is allowed or not using path mappings


`setConfigPropFile(String)`:
* This method is used to set the configuration file, `server.properties`.
* Parameters:
  * `String`: configuration file path
* Note that the absolute path for the file must be entered, i.e., `/home/testuser/demo_server/server.properties`

`start()`:
* Starts the server
* The server is multi-threaded, in other words, when a client connects to the server, a new thread is spawned and pushed to the Thread queue. `ExecutorService` class is used to implement the Thread queue.
  * For every client, a `HttpController` Thread is spawned and executed by `ExecutorService`. This ensures that the calls are non blocking.
* This method waits for a client to connect to the server. Every client socket connection is closed after the HTTP response has been sent.
* This method creates a [`com.egehurturk.handlers.HttpHandler`](#comegehurturkhandlershttphandler) class and registers it. Thus, the user doesn't have to add this handler manually.


`stop()`:
* Stops the server
* Closes all the connections:
  * `ServerSocket` connection
  * `InputStream` for `Properties`


`addHandler(Methods, String, Handler)`:
* This method is used to add a `Handler` class to the server. (See [`com.egehurturk.handlers.Handler`](#comegehurturkhandlershandler))
* Parameters:
  * `Methods`: a value in [`com.egehurturk.util.Methods`](#comegehurturkutilmethods) enum. This should be `Methods.GET` as of **Banzai v1.0**
  * `String`: path associated with the handler. This is the path that the `Handler` is going to be executed on.
  * `Handler`: Any class that implements [`com.egehurturk.handlers.Handler`](#comegehurturkhandlershandler).

`configureServer() throws ConfigurationException`:
* This method is used to configure the server (not setting the configuration file path, but to configure). Calls [`com.egehurturk.core.BaseServer`](#comegehurturkcorebaseserver)'s `configureServer()` method.
* If the file is not found, then, [`com.egehurturk.exceptions.ConfigurationException`](#comegehurturkexceptionsconfigurationexeption) is thrown.

## `com.egehurturk.httpd.HttpRequest`
This class is a data structure for storing HTTP request messages.

This class is abstracted from the user of the API. You don't need to create a new HttpRequest. The only place you will have access to a request is in `Handler`s. Look at [`com.egehurturk.handlers`](#comegehurturkhandlers) package for more information.

### Anatomy of HTTP Requests
An HTTP request is a message in the following format:
1. Request line
2. Zero or more headers + CRLF
3. CRLF (Empty line)
4. Optional message body

Here, CRLF is `\r\n` . A request-line begins with a **method**, followed by the request URI, HTTP version, and CRLF.
```
GET /hello HTTP/1.1\r\n
```

Here, `GET` is the HTTP method, `/hello` is the request URI, and `HTTP/1.1` is the HTTP version.

A complete list of request methods can be found [here](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods). Since Banzai currently (as of `v.10` ) only supports `GET` requests, in this documentation, `GET` requests will be discussed.

A `GET` request requests for a source, then the server returns a `HTML` file as a response. HTTP requests include zero or more headers that stores information about the client. Here is the complete list of header values.

### Constructors
`HttpResponse(BufferedReader) throws HttpRequestException, IOException`: constructors a HTTP request with the client's `InputStream` wrapped in `BufferedReader`.

Exceptions:
* If the `BufferedReader` is null, `HttpRequestException` is thrown.
* If the request line of HTTP request( `METHOD + PATH + HTTP` ) does not exists, `HttpRequestException` is thrown.
* If the request is not valid, `HttpRequestException` is thrown.
* If there are invalid headers, `HttpRequestException` is thrown.

### Important Methods
`toMap()`:
* Constructs a `HashMap` from the class. The map includes every header and the request
line

`hasHeader(String)`:
* If the given key exists in the request, `true` is returned. If not, `false` is returned.

`getHeader(String)`:
* Retrieve the header value from the given key.
  * If the key exists, then the value for the header is returned
  * If the key does not exist, then `null` is returned.
    * It is recommended to use `hasHeader(String)` method before using this method to avoid `null` values

`hasParamater(String)`:
* This method checks if the given query parameter actually exists in URL. Returns a boolean

`getParameter(String)`:
* Retrieve the query parameter value from URL.
  * If the parameter exists, the value is returned
  * If not,  `null` is returned.
    * Again, it is recommended to use `hasParameter(String)` to avoid `null` values.

### Example Usage:

```java
String body;
if (request.hasParameter("name")) {
    body = request.getParameter("name");
} else {
    body = "<h3><i>no value</i></h3>";
}
```

## `com.egehurturk.httpd.HttpResponse`

This class is a data structure for storing HTTP responses.
### Anatomy of HTTP Responses
After receiving an HTTP request, the server prepares and sends an HTTP response in the following format:
1. Status line
2. Zero or more headers + CRLF (`\r\n`)
3. CRLF (`\r\n`)
4. Message body


A response begins with a **status line**. The status line consists of the HTTP version, status code, and the status message.
```
HTTP/1.1 200 OK
```

Here, HTTP/1.1 is the HTTP version, 200 is the status code, and OK is the status message. A complete list of status codes and messages can be found [here](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status).


### Factory Methods

`public HttpResponse create(HashMap<String, String>, String, int, String, byte[], PrintWriter)`: constructors a HTTP response with the headers (`HashMap`), HTTP Scheme, Status code, message, response body (in `byte` array), and the `PrintWriter` for the client. This factory method is not recommended as there are many parameters. Use [`com.egehurturk.httpd.HttpResponseBuilder`](#comegehurturkhttpdhttpresponsebuilder).

Exceptions:
* If the status code and message does not exist or not valid, `NotImplemented501Exception` exception is thrown.

### Important Methods
`set(K, V)`:
* Creates a new entry in headers. Puts `K: V`.


## `com.egehurturk.httpd.HttpResponseBuilder`
This class is a builder that builds [`com.egehurturk.httpd.HttpResponse`](#comegehurturkhttpdhttpresponse)

### Example Usage

```java
class MHandler implements com.egehurturk.handlers.Handler {
  @Override
  public HttpResponse handle(HttpRequest request, HttpResponse response) {
      String body = "<h1>Hello!</h1>";
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
```


## `com.egehurturk.httpd.EntryPoint`
This class is the entry point for Banzai. The JAR (`BanzaiServer-1.0-SNAPSHOT.jar`) is configured to have the main class as this class. You should not use this class if you are using the API; however, this class contains examples for some handlers and you can take a quick look to the examples.

# `com.egehurturk.annotations`
This package is about Banzai annotations.

## `com.egehurturk.annotations.BanzaiHandler`


Marks the class as a handler container that takes part in the HTTP request-response flow. Any class that is marked with this annotation is considered to be a handler container, that is, containing methods with [`com.egehurturk.annotations.HandlerMethod`](#comegehurturkannotationshandlermethod) annotated-methods.

In contrast to the interface-based [`com.egehurturk.handlers.Handler`](#comegehurturkhandlershandler) approach, this annotation groups methods. Instead of creating a class that implements `Handler` for every path, this approach groups different methods and each method that is annotated with `HandlerMethod` acts like a class that implements `Handler`.


The client registers a class that is annotated with this annotation with [`com.egehurturk.httpd.HttpServer`](#comegehurturkhttpdhttpserver)'s `addHandler(Class clazz)` method.

Example:

```java
@BanzaiHandler
public class MyHandler {
    @HandlerMethod(path = "/jimi_hendrix")
    public static HttpResponse handle_JIMI(HttpRequest req, HttpResponse res) {
        HttpResponse resp = // ...
        // ...
        return resp;
    }

    @HandlerMethod(path = "/metallica")
    public static HttpResponse handle_METALLICA(HttpRequest req, HttpResponse res) {
        HttpResponse resp = // ...
        // ...
        return resp;
      }
}

// client's entry point class
HttpServer s = // ...;
s.addHandler(MyHandler.class); // this will throw MalformedHandlerException.

```

## `com.egehurturk.annotations.HandlerMethod`
Marks the method as a [`com.egehurturk.handlers.Handler`](#comegehurturkhandlershandler) that regulates the HTTP request-response flow. Any method that is marked with this annotation is considered to be a Handler.

Each method which is annotated by this annotation is similar to a class that implements Handler. The former includes a method called handle that takes in [`com.egehurturk.httpd.HttpRequest`](#comegehurturkhttpdhttprequest) and [`com.egehurturk.httpd.HttpResponse `]((#comegehurturkhttpdhttpresponse)) and returns `HttpResponse`.

 Annotating a method with this annotation is similar to the former approach - except, one does not need to create a class.

This annotation annotated methods must be in a class which is annotated by `BanzaiHandler`. This is a must since handler methods should be grouped together and that annotation makes this possible.

Note that every method that is annotated by this annotation **should be declared static.** However, it doesn't matter whether the method is `private`, `protected`, or `public`.

Example:

```java
@BanzaiHandler
public class MyHandler {
    @HandlerMethod(path = "/jimi_hendrix")
    public static HttpResponse handle_JIMI(HttpRequest req, HttpResponse res) {
        HttpResponse resp = // ...
        // ...
        return resp;
    }

    @HandlerMethod(path = "/metallica")
    public static HttpResponse handle_METALLICA(HttpRequest req, HttpResponse res) {
        HttpResponse resp = // ...
        // ...
        return resp;
      }
}

// client's entry point class
HttpServer s = // ...;
s.addHandler(MyHandler.class); // this will throw MalformedHandlerException.

```


# `com.egehurturk.handlers`
This package is about how Banzai maps certain `Handler`s to URLs and retrieve/send documents to the client.

## `com.egehurturk.handlers.ResponseType`
All response types should implement this interface. Response types are similar to plug-ins. Every response type can be instantiated and used. All response types have a method to construct `HttpResponse` to be sent to the client.

### Important Methods
` HttpResponse toHttpResponse()`: Convert the response type into `HttpResponse` so that the client can return the result in a `Handler`.

## `com.egehurturk.handlers.FileResponse`
Encapsulates everything concerning file handling. This class should be used whenever a document needs to be served.

### Constructors
`FileResponse(InputStream, PrintWriter)`: Constructs this class with the stream of the document and the print writer of the client.

`FileResponse(String, PrintWriter)`: Constructs this class with the path of the document and the print writer of the client.

* Note: the path (`String` argument) should be an absolute path. This is not a bug, because it enables to serve documents that are outside of webroot.

### Examples
```java
class MHandler implements com.egehurturk.handlers.Handler {
    @Override
    public HttpResponse handle(HttpRequest request, HttpResponse response) {
        FileResponse fil = new FileResponse("/home/test_user/hey.html", response.getStream());
        return fil.toHttpResponse();
    }
}
```
It is that easy to serve static files. You only need to pass the path and return it as `HttpResponse` via `toHttpResponse()`.

## `com.egehurturk.handlers.JsonResponse`
This class is a helper class to return `JSON` type as a response. You can use `FileResponse` to return `json` files, e.g., `hey.json`. However, that class won't check whether the client allows `JSON` format as a response. Always use this class to return `JSON` responses.

### Constructors
`JsonResponse(PrintWriter, String)`: Constructs this class with the print writer of client and the JSON body.

`JsonResponse(PrintWriter)`: Constructors this class with the print writer of client.

`JsonResponse(PrintWriter, HttpRequest)`: Constructs this class with the print writer of client and the http request. `HttpRequest` is important to validate the request.

### Important Methods
`validate(HttpRequest)`: Checks if the request has the header `Accept: */*` or `Accept: application/json`. If the request lacks these headers, `406 Not Acceptable` response is returned.
* Using the constructor with `HttpRequest` calls this method in the constructor, so you don't need to call this method again. Thus, it is recommended to use the constructor with the parameters `PrintWriter, HttpRequest`.


### Examples:
```java
class MHandler implements com.egehurturk.handlers.Handler {
    @Override
    public HttpResponse handle(HttpRequest request, HttpResponse response) {
        JsonResponse json = new JsonResponse(response.getStream(), request); // automatically validated
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
        return json.toHttpResponse();
    }
}
```

## `com.egehurturk.handlers.Handler`
Interface for connection handlers. Each and every connection, in http protocol, should implement the `HttpResponse handle(HttpRequest, HttpResponse)` method.

> Glossary: I am using the term "handler class" for any class that implements this interface

Any handler class should accept `HttpRequest, HttpResponse` and return a `HttpResponse`.

Note that users create their own handlers and bind these handlers to specific URLs.

# `com.egehurturk.util`
This package is a utility package that provides useful methods & classes.
## `com.egehurturk.util.ArgumentParser`
This class is a wrapper for Apache Commons CLI. You should not use this class.
## `com.egehurturk.util.HeaderStatus`
This enum provides fields about status of headers, i.e., where the header is used. The fields are:
* `General`, indicating that the header can be used in both responses and requests
* `Request`, indicating that the header is used in requests only
* `Response`, indicating that the header is used in responses only

## `com.egehurturk.util.Headers`
This enum provides fields for HTTP headers. These fields are used in [`HttpResponseBuilder`](#comegehurturkhttpdhttpresponsebuilder).

#### Constructor Summary
`Headers(String NAME, HeaderStatus place)`

#### Fields
* `Headers.CONNECTION`
* `Headers.ACCEPT`
* `Headers.ACCEPT_LANGUAGE`
* `Headers.ACCEPT_ENCODING`:
* `Headers.HOST`
* `Headers.SERVER`
* `Headers.USER_AGENT`
* `Headers.DATE`
* `Headers.CONTENT_TYPE`
* `Headers.CONTENT_LENGTH`
* `Headers.CONTENT_ENCODING`
* `Headers.CONTENT_LANGUAGE`

You can access the names of fields with:
```java
Headers.CONNECTION.NAME
```
This will give the name of the header, which is `"Connection: "` in this case.

## `com.egehurturk.util.Methods`
This is an `enum` that stores HTTP methods.

Instances:
* `Methods.GET`
* `Methods.POST`

# `com.egehurturk.renderers`
This package is about HTML template renderers.
## `com.egehurturk.handlers.HTMLRenderer`
This class is used to create **dynamic** HTML templates. Dynamic means that the contents of the HTML file can change during run time.

This class parses HTML containing tags and replaces tags (`[@ ... ]`) with values taken from the user.

### Constructors
`HTMLRenderer(String, PrintWriter)`: constructs this class with the path of the HTML file and the print writer of client.

### Important Methods

`String render()`: Renders the HTML document, replacing tags (`[@ ... ]`) with the variables set by user.
* Return value: HTML string.

`HttpResponse toHttpResponse()`: converts this class to `HttpResponse`.

`setVar(String argInHtml, String val)`: sets the argument
* Parameters:
  * `argInHtml`: the argument name declared in the HTML source file
  * `val`: the value of the argument


### Examples:
The HTML file:

```html
<h1>[@username] profile</h1>
<b>Name:</b> [@name]<br />
<b>Age:</b> [@age]<br />
<b>Location:</b> [@location]<br />
```

```java
static class MHandler implements com.egehurturk.handlers.Handler {
    @Override
    public HttpResponse handle(HttpRequest request, HttpResponse response) {

        HTMLRenderer contentRenderer = new HTMLRenderer("/home_test_user/hey.html", response.getStream());

        contentRenderer.setVar("username", "monkey");
        contentRenderer.setVar("name", "Monkey man");
        contentRenderer.setVar("age", "23");
        contentRenderer.setVar("location", "Turkey");
        return userRenderer.toHttpResponse();
    }
}
```

The HTML file will become:

```html
<h1>monkey profile</h1>
<b>Name:</b> Monkey man<br />
<b>Age:</b> 23<br />
<b>Location:</b> Turkey<br />
```

You can even create separate templates and use these templates inside each other. For instance:

`user.html`:
```html
<!DOCTYPE html>
<html>
<head>
    <title>[@title]</title>
</head>
<body>
<div id="menu">
    <h1>Navigation</h1>
</div>
<div id="content">
    [@content]
</div>
</body>
</html>
```

`dist.html`:
```html
<h1>[@username] profile</h1>
<b>Name:</b> [@name]<br />
<b>Age:</b> [@age]<br />
<b>Location:</b> [@location]<br />
<div class="footer_clear"></div>
```

```java
static class MHandler implements com.egehurturk.handlers.Handler {
    @Override
    public HttpResponse handle(HttpRequest request, HttpResponse response) {

        HTMLRenderer contentRenderer = new HTMLRenderer("/home/test_user/dist.html", response.getStream());

        contentRenderer.setVar("username", "monkey");
        contentRenderer.setVar("name", "Monkey man");
        contentRenderer.setVar("age", "23");
        contentRenderer.setVar("location", "Turkey");

        HTMLRenderer userRenderer = new HTMLRenderer("home/test_user/user.html", response.getStream());
        userRenderer.setVar("title", "User Profile");
        userRenderer.setVar("content", contentRenderer.render()); // **
        return userRenderer.toHttpResponse();
    }
}
```

The java code renders the first HTML (`dist.html`) and fills in the `[@content]` tag of `user.html` with the value of the rendered `dist.html`. The result will be:

```html
<!DOCTYPE html>
<html>
<head>
    <title>User Profile</title>
</head>
<body>
<div id="menu">
    <h1>Navigation</h1>
</div>
<div id="content">
    <h1>monkey profile</h1>
    <b>Name:</b> Monkey man<br />
    <b>Age:</b> 23<br />
    <b>Location:</b> Turkey<br />
</div>
</body>
</html>
```


# `com.egehurturk.exceptions`
This package is about exceptions.

> Note that these exceptions are not handled by the user. All these exceptions are caught by the server.
## `com.egehurturk.exceptions.ConfigurationException`
Exception class that extends `Exception`. This exception is thrown if there is an error related to configuration phase. The reasons may include:

* Configuration file cannot be found in the local file system
* Configuration file contains unidentified properties

## `com.egehurturk.exceptions.FileSizeOverflowException`
Exception class that extends `Exception`. This exception is thrown if the size of the file that is requested is greater than a constant, `20000000000L`.

## `com.egehurturk.exceptions.HttpRequestException`
Exception class that extends `Exception`. This exception is an abstract class and its child classes are exceptions about HTTP requests.


## `com.egehurturk.exceptions.HttpResponseException`
Exception class that extends `Exception`. This exception is an abstract class and its child classes are exceptions about HTTP responses.


## `com.egehurturk.exceptions.HttpVersionNotSupportedException`
This exception is thrown if the HTTP version of the request is not supported.





