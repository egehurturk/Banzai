- [HTTP](#http)
  - [About](#about)
  - [Headers](#headers)
  - [Life Cycle](#life-cycle)
# HTTP

## About
Banzai is a server, specifically, a Web Server, that uses `HTTP/1.0`, and `HTTP/1.1` to communicate with the client. `HTTP/2.0` and `HTTPS` protocols are not supported, as of the current version (V1.0).

## Headers
`HTTP` protocol includes headers, *key-value* pairs, in HTTP Requests and HTTP Responses. These headers include:
* `Host` 
* `Server`
* `Date`
* `Content-Language`
* `Content-Length`
* `Content-Encoding`
* and more

You can refer to [Mozilla's HTTP documentation](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers) for more information about HTTP headers.

Banzai supports for:
* `Server`
* `Host`
* `Date`
* `Content-Language`
* `Content-Length`
* `Accept` (in Requests)

An HTTP request must contain the request line, in the format:
```
METHOD PATH PROTOCOL
```

For example,

```
GET / HTTP/1.1
```

If a request does not contain this request line, or is wrongly formatted (not containing PROTOCOL or wrong METHOD), then Banzai responds with a `400 Bad Request`  HTTP response. Here're some examples for malformed requests:

`HEY / HTTP/1.1`
`GET / SMTP`
`GET /hey HTTP`
...

If the request is malformed, then Banzai logs an error to the console:

![error](../external/errorlogfile.png)

Banzai also requires the `Host` header to be included in a request. Typically, all browsers will include this header. If the `Host` header is not present in a request, then Banzai will respond with a `400 Bad Request` HTTP Response.

When using the `JsonResponse` class, a request must contain:
```
Accept: application/json
```

If a request lacks this header, then Banzai will respond with a `406 Not Acceptable` HTTP Response.



## Life Cycle
Banzai regulates HTTP protocol through `HttpResponse` and `HttpRequest` headers. These headers are a data structure for storing raw HTTP string from the client's `InputStream`. 

`HttpResponse` and `HttpRequest` provide an abstraction over the HTTP protocol. You'll rarely need to instantiate one of these classes. You'll only encounter `HttpResponse` and `HttpRequest` in a custom class that implements `Handler`. The method `handle` takes `HttpRequest` and `HttpResponse`, and returns a `HttpResponse`.

`FileResponse`, `JsonResponse`, and `HTMLRenderer` classes provide a method to convert their content into `HttpResponse`. Here's an example:

```java

class MyHandler implements Handler {
	@Override
	public HttpResponse handle(HttpRequest req, HttpResponse res) {
            FileResponse fil = new FileResponse("/some.html", res.getStream()); 
            HttpResponse res = fil.toHttpResponse()
            return res;
	}
}
```
* Here, `res.getStream()` is required for any of the *response* classes discussed above (`FileResponse`, `JsonResponse`, and `HTMLRenderer`). The method returns the output stream of the client
* A `FileResponse` was instantiated and then converted into `HttpResponse` through the method `toResponse()`. 

Similarly, when constructing a `JsonResponse`, you'll need the `HttpRequest` object to check if the raw HTTP request contains the header `Accept: application/json`:

```java
class MyHandler implements Handler {
	@Override
	public HttpResponse handle(HttpRequest req, HttpResponse res) {
            JsonResponse json = new JsonResponse(res.getStream(), req); 
            jes.setBody("{Hello!}");
            return json;
	}
}
```
* Here, the `HttpRequest` is needed for `JsonResponse` to validate the request

