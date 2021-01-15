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
       
     
DEBUG MESS CLEAN:
- HttpServer:
    * 240-255 
    * 476, [488, 492]
    * 497
    * 501
    * 509 
    * 511
    * 513
    * [516, 520]
    * 523, 525
    * 528, 530
    * 533, 535
    * 537, 539, 541
    * 593, 596, 598, 599
    * 602, 604, 606, 610, 614, 617, 619, 624, 628, 630
    * 637, 641, 645, 647, 649, 654, 657, 659, 
    * 661, 666, 668, 670, 672, 674, 678, 680, 683, 686
    * 724, 726, 730, 732, 743, 745, 748
    * [754, 762]
    * 776
    
- Utility:
- HeaderEnum: 
- HttpResponseBuilder:
- HttpResponse:
    * 99, 104, 110, 116, 122, 127, 128
- HttpRequest:
    * 114, 129, 136, 137, 143, 152, 153, 163, 164, 165
    * 170, 171, 172, 174, 189, 225, 226, 227, 235
- DriverClassForTest:
    * 11  