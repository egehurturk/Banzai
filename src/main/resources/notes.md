* 200 OK ->  OK when everything is OK.
* 400 Bad Request-> Server cannot process due to
                                           client error
* 403 Forbidden: Server does not allow client to access the page because of not having  necessary permissions for a resource
* 404 Not Found 
* 405 Not Allowed:  The method that accesses the URL is not allowed. E.g. accessing a page with GET request that accepts only POST request
* 500 Internal: A generic error message when something bad happens inside the server
* 501 Not Implemented: Server lacks to fulfill the request. Usually this implies future avaliability (e.g. a new feature)

---
* "Connection: ": controls whether or not the network connection stays open after the current
                                       transaction finishes. If the value sent is keep-alive, the connection is
                                      persistent and not closed, allowing for subsequent requests to the same server to be done.    
* "Accept: ": advertises which content types, expressed as MIME types,
                                the client is able to understand. Using content negotiation, the server then selects one of the proposals,
                                uses it and informs the client of its choice with the Content-Type response header.
       
     