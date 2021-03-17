# Installing Banzai

## Banzai on Docker
### Steps:
* Clone this repository 

```bash
$ git clone https://github.com/egehurturk/HttpServer.git
``` 


* `cd`'to the directory you cloned 

```bash
$ cd HttpServer 
```

* Change permissions for `run_docker.sh`

```bash
$ chmod 755 run_docker.sh
``` 


* Create a directory that is going to be volumed in the Docker container
```bash
$ cd ..
$ mkdir test_server
``` 

* `cd` into the newly created directory
```bash
$ cd test_server
```

* Add a `server.properties` configuration file and pass in the configurations
```bash
$ touch server.properties
``` 

* Edit `server.properties` in your editor (vim is used here)
```bash
$ vim server.properties
```
* Then add the following configuration into `server.properties`
    * Note that you should delete the comments, i.e. `server.properties` must only include key-value pairs (key = pair)
    * Also note that the webroot must start with "/" and should include the volume's name, `test_server` in this case
```properties
server.port = 9090 # this will be the port that Banzai runs on
server.host = 0.0.0.0 # this will be the host that Banzai runs on
server.name = Test_Software # this will be the name of webserver
server.webroot = /test_server/www # this will be the webroot of the server
debug = false # controls whether the debug mode is enabled or not. 
```

* Create a directory with the name webroot you passed in the configuration file
```bash
$ mkdir www
``` 

* Create some `html` files in the directory
```bash
$ cd www
$ touch index.html
$ echo "<h1>Hello, World! This is index.html</h1>" > index.html
$ touch banzai.html
$ echo "<h1>This is <a href="www.github.com/egehurturk/HttpServer">Banzai</a> </h1>" > banzai.html
```
* Run `run_docker.sh` with the absolute path for the volume you created
    * Note that the path must be an absolute path and should point to the directory you want to serve
```bash
$ cd .. && cd .. && cd HttpServer
$ ./run_docker.sh -d "/Users/$USER/test_server"
``` 

These steps will create a Docker container and run the server inside the docker container. Any changes you made to the directory you want to serve, `test_server` in this case, will be updated on the container.

You can skip the "clean build" for the project with inputting "N" for the prompt. 
