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
  - [`com.egehurturk.httpd.HttpResponseBuilder`](#comegehurturkhttpdhttpresponsebuilder)
  - [`com.egehurturk.httpd.EntryPoint`](#comegehurturkhttpdentrypoint)
- [`com.egehurturk.handlers`](#comegehurturkhandlers)
  - [`com.egehurturk.handlers.HttpController`](#comegehurturkhandlershttpcontroller)
  - [`com.egehurturk.handlers.HttpHandler`](#comegehurturkhandlershttphandler)
  - [`com.egehurturk.handlers.ResponseType`](#comegehurturkhandlersresponsetype)
  - [`com.egehurturk.handlers.FileResponse`](#comegehurturkhandlersfileresponse)
  - [`com.egehurturk.handlers.JsonResponse`](#comegehurturkhandlersjsonresponse)
  - [`com.egehurturk.handlers.Handler`](#comegehurturkhandlershandler)
- [`com.egehurturk.util`](#comegehurturkutil)
  - [`com.egehurturk.util.ArgumentParser`](#comegehurturkutilargumentparser)
  - [`com.egehurturk.util.HeaderStatus`](#comegehurturkutilheaderstatus)
  - [`com.egehurturk.util.Headers`](#comegehurturkutilheaders)
      - [Constructor Summary](#constructor-summary-1)
      - [Fields](#fields)
  - [`com.egehurturk.util.Methods`](#comegehurturkutilmethods)
  - [`com.egehurturk.util.Pair`](#comegehurturkutilpair)
  - [`com.egehurturk.util.Status`](#comegehurturkutilstatus)
  - [`com.egehurturk.util.Utility`](#comegehurturkutilutility)
- [`com.egehurturk.renderers`](#comegehurturkrenderers)
  - [`com.egehurturk.renderers.HTMLRenderer`](#comegehurturkrenderershtmlrenderer)
- [`com.egehurturk.core`](#comegehurturkcore)
  - [`com.egehurturk.core.BaseServer`](#comegehurturkcorebaseserver)
- [`com.egehurturk.exceptions`](#comegehurturkexceptions)
  - [`com.egehurturk.exceptions.BadRequest400Exception`](#comegehurturkexceptionsbadrequest400exception)
  - [`com.egehurturk.exceptions.ConfigurationExeption`](#comegehurturkexceptionsconfigurationexeption)
  - [`com.egehurturk.exceptions.FileSizeOverflowException`](#comegehurturkexceptionsfilesizeoverflowexception)
  - [`com.egehurturk.exceptions.HttpRequestException`](#comegehurturkexceptionshttprequestexception)
  - [`com.egehurturk.exceptions.HttpResponseException`](#comegehurturkexceptionshttpresponseexception)
  - [`com.egehurturk.exceptions.HttpVersionNotSupportedException`](#comegehurturkexceptionshttpversionnotsupportedexception)
  - [`com.egehurturk.exceptions.MethodNotAllowedException`](#comegehurturkexceptionsmethodnotallowedexception)
  - [`com.egehurturk.exceptions.NotFound404Exception`](#comegehurturkexceptionsnotfound404exception)
  - [`com.egehurturk.exceptions.NotImplemented501Exception`](#comegehurturkexceptionsnotimplemented501exception)

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
No Args Constructor:
* The constructor initializes all objects to avoid `NullPointerException`.
* Other than that, the constructor does nothing. 
* The purpose of this constructor is to configure `HttpServer` via a `server.properties` file. 
* This is the recommended way to instantiate this class.

File Config Path constructor:
* Accepts the configuration file (`server.properties`) path.
* The constructor then sets the configuration file path as the given value, and calls the `configureServer` method to configure the server. 

#### Important Methods

`allowCustomUrlMapping(boolean)`:
* This method enables the server to allow for custom URL mappings. 
  * In other words, certain paths can be mapped to certain Handlers. 
  * For example, the path `/hello` may be mapped to `MyHandler` class that implements the `Handler` interface
  * Parameters:
    * `boolean allow`: whether it is allowed or not using path mappings
* Parameters:
  * `boolean`: true for allowing custom URL mapping, false for forbidding.
  
`setConfigPropFile(String)`:
* This method is used to set the configuration file, `server.properties`. 
* Parameters:
  * `String`: configuration file path
* Note that the absolute path for the file must be entered, i.e., `/home/testuser/demo_server/server.properties`
  * Instructions for Docker may vary, see [Docker](Docker.md)

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
  * `Properties Stream` connection


`addHandler(Methods, String, Handler)`:
* This method is used to add a custom `Handler` class to the server. 
* Parameters are:
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


## `com.egehurturk.httpd.HttpResponseBuilder`
## `com.egehurturk.httpd.EntryPoint`
This class is the entry point for Banzai. The JAR (`BanzaiServer-1.0-SNAPSHOT.jar`) is configured to have the main class as this class. You should not use this class if you are using the API; however, this class contains examples for some handlers and you can take a quick look to the examples. 
# `com.egehurturk.handlers`
This package is about how Banzai maps certain `Handler`s to URLs and retrieve/send documents to the client. 

## `com.egehurturk.handlers.HttpController`

## `com.egehurturk.handlers.HttpHandler`
## `com.egehurturk.handlers.ResponseType`
## `com.egehurturk.handlers.FileResponse`
## `com.egehurturk.handlers.JsonResponse`
## `com.egehurturk.handlers.Handler`

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

## `com.egehurturk.util.Pair`
Represents a pair. 
## `com.egehurturk.util.Status`

## `com.egehurturk.util.Utility`


# `com.egehurturk.renderers`
## `com.egehurturk.renderers.HTMLRenderer`

# `com.egehurturk.core`
## `com.egehurturk.core.BaseServer`

# `com.egehurturk.exceptions`

## `com.egehurturk.exceptions.BadRequest400Exception`
Exception class that extends `Exception`. This exception is thrown if the HTTP request is a bad request, i.e., containing malformed parts. 


## `com.egehurturk.exceptions.ConfigurationExeption`
Exception class that extends `Exception`. This exception is thrown if there is an error related to configuration phase. The reasons may include:

* Configuration file cannot be found in the local file system
* Configuration file contains unidentified properties

## `com.egehurturk.exceptions.FileSizeOverflowException`
Exception class that extends `Exception`. This exception is thrown if the size of the file that is requested is greater than a constant, `20000000000L`.



## `com.egehurturk.exceptions.HttpRequestException`
Exception class that extends `Exception`. This exception is an abstract class


## `com.egehurturk.exceptions.HttpResponseException`
Exception class that extends `Exception`. This exception is thrown if there is an error related to configuration phase. The reasons may include:

* Configuration file cannot be found in the local file system
* Configuration file contains unidentified properties


## `com.egehurturk.exceptions.HttpVersionNotSupportedException`
Exception class that extends `Exception`. This exception is thrown if there is an error related to configuration phase. The reasons may include:

* Configuration file cannot be found in the local file system
* Configuration file contains unidentified properties


## `com.egehurturk.exceptions.MethodNotAllowedException`
Exception class that extends `Exception`. This exception is thrown if there is an error related to configuration phase. The reasons may include:

* Configuration file cannot be found in the local file system
* Configuration file contains unidentified properties


## `com.egehurturk.exceptions.NotFound404Exception`
Exception class that extends `Exception`. This exception is thrown if there is an error related to configuration phase. The reasons may include:

* Configuration file cannot be found in the local file system
* Configuration file contains unidentified properties
## `com.egehurturk.exceptions.NotImplemented501Exception`
Exception class that extends `Exception`. This exception is thrown if there is an error related to configuration phase. The reasons may include:

* Configuration file cannot be found in the local file system
* Configuration file contains unidentified properties




