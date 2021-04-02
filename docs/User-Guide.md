### Table of Contents
- [`com.egehurturk.httpd`](#comegehurturkhttpd)
  - [`com.egehurturk.httpd.HttpServer`](#comegehurturkhttpdhttpserver)
      - [Constructor Summary](#constructor-summary)
      - [Important Methods](#important-methods)
  - [`com.egehurturk.httpd.HttpRequest`](#comegehurturkhttpdhttprequest)
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
  - [`com.egehurturk.util.Json`](#comegehurturkutiljson)
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
## `com.egehurturk.httpd.HttpResponse`
## `com.egehurturk.httpd.HttpResponseBuilder`
## `com.egehurturk.httpd.EntryPoint`

# `com.egehurturk.handlers`
## `com.egehurturk.handlers.HttpController`
## `com.egehurturk.handlers.HttpHandler`
## `com.egehurturk.handlers.ResponseType`
## `com.egehurturk.handlers.FileResponse`
## `com.egehurturk.handlers.JsonResponse`
## `com.egehurturk.handlers.Handler`

# `com.egehurturk.util`

## `com.egehurturk.util.ArgumentParser`
## `com.egehurturk.util.HeaderStatus`
## `com.egehurturk.util.Headers`
## `com.egehurturk.util.Json`
## `com.egehurturk.util.Methods`
## `com.egehurturk.util.Pair`
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




