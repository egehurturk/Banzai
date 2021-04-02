### Table of Contents
- [Configuration](#configuration)
  - [Configuration With `Properties` File](#configuration-with-properties-file)
    - [Setting Configuration File through Banzai API](#setting-configuration-file-through-banzai-api)


# Configuration


## Configuration With `Properties` File
Banzai can be configured with a `properties` file. The file should have `.properties` extension, containing a *key-value* pair at each line separated by a new line character (\n). Here's an example `properties` file for configuring Banzai:

```properties
server.port = 9090
server.host = 0.0.0.0
server.name = Banzai
server.webroot = <absolute_path_to_directory>
debug = false
```

These five *key-value* pairs are enough to configure Banzai. Each of the keys is explained:
* `server.port`: The port that Banzai is going to run
* `server.host`: The host that Banzai is going to run
  * It is convenient to set `server.host` to `0.0.0.0`; this means that Banzai will listen to the default host
* `server.name`: The name of the `server` field that will be displayed on HTTP Response
* `server.webroot`: Webroot that Banzai will use
* `debug`: `true` or `false`. `true` will print a verbose output

**Note:** `server.webroot`'s value depends on:
* Whether Banzai will run on a `docker` container or locally

If Banzai is going to be run on `docker`, that is, the client calls `run_docker.sh`, then the value for `server.webroot` **must** be the name of the project containing `server.properties` and the webroot directory, plus the name of the webroot directory. Here's an example:
* `server.webroot = /test_server/www`
  * Assuming `test_server` on host contains `server.properties` and the directory `www`
  * Here, `www` is not obligatory; it can be named anything. However, using `www` as webroot is recommended.
  * Note the `/`. This is a **must** because the volume (i.e. `test_server` on local host) is going to be mounted on `/` (root) directory on the `docker` container (and the script to run banzai will look for that directory, i.e. `/test_server`); thus, you should include `/`.

If Banzai is going to be run locally, the client clones this repository and uses the `JAR` as a `Maven` dependency, then the value for `server.webroot` **must** be the absolute path for the directory you marked as webroot. Here's an example:
* `server.webroot = /home/testuser/test_server/www`
  * Assuming `test_server` contains the directory `www`. Note that this directory may not contain the `server.properties` file. The client will set the location for `server.properties` through mutator methods in Java code.
  * Here, `www` is not obligatory; it can be named anything. However, using `www` as webroot is recommended. 

Here's an example for `server.properties`:

```properties
server.port = 7095
server.host = 0.0.0.0
server.name = Banzai
server.webroot = /home/egehurturk/test_banzai/www
debug = true
```

Note that `server.properties` should not include any comments. 
<details>
<summary>Why?</summary>

Because the script `/scripts/parser.sh` used to parse the `properties` file includes comments if the file contains comments. Thus, you should not include any comments in `server.properties`. 

</details>


### Setting Configuration File through Banzai API
If you will use Banzai without docker and running locally, you need to set the `server.properties`'s path as configuration path of the server. In other words, you should use the method `setConfigPropFile(String)` of class `com.egehurturk.httpd.HttpServer`. Here's an example:

```java
HttpServer server = new HttpServer();
server.setConfigPropFile("/home/egehurturk/test_banzai/server.properties");
```
Then, you should use the `configureServer()` method of class `com.egehurturk.httpd.HttpServer`. Here's an example:

```java
try {
    server.configureServer()
} catch (ConfigurationException err) {
    err.printStackTrace();
}
```

You can use Banzai's default behavior, i.e., using Banzai to serve documents inside the `webroot` defined in `server.properties` file as `server.webroot`. Then, access the documents via including the file name in URL, e.g., `localhost:9095/file1.html`. 

Note that the docker way only uses this behavior of Banzai, as stated in [User Guide](User-Guide.md). 

However, if you use Banzai locally, then you can configure whether the server allows custom URLs; in other words certain paths can be mapped to certain documents in local filesystem. You can set this behavior through the `allowCustomUrlMapping(boolean)` method of class `com.egehurturk.httpd.HttpServer`. Here's a demonstration:

```java
server.allowCustomUrlMapping(true); // this will allow for custom URLs
```

After this, any `Handler` (class that implements the `com.egehurturk.handlers.Handler` interface) can be used with:
```java
server.addHandler(Methods.GET, "/jimi", new MyHandler());
```
* Assuming that `MyHandler` class implements the interface `Handler`
* Here, a request to the path `localhost:8090/jimi` (assuming `8090` is configured as port in `server.properties`) will use `MyHandler` class. Here's an example implementation of `MyHandler` class:
  ```java
  class MyHandler implements com.egehurturk.handlers.Handler {
      @Override
      public HttpResponse handle(HttpRequest req, HttpResponse res) {
          // Absolute path is used
          FileResponse fileR = new FileResponse("/home/egehurturk/test_banzai/www/jimi_hendrix.html", res.getStream()); 
        return fileR.toHttpResponse();
      }
  }
  ```

  * The HTML file location in `FileResponse` must be the absolute path to the document. 
    * This may be viewed as a "bug"; however, absolute path enables serving other documents outside of `webroot`. 
    * Refer to [User Guide](User-Guide.md) for more information about `FileResponse`



  