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
    * Note that `server.properties` must only include key-value pairs (key = pair), not any comments
    * Also note that the webroot must start with "/" and should include the volume's name, `test_server` in this case
```properties
server.port = 9090 
server.host = 0.0.0.0 
server.name = Test_Software 
server.webroot = /test_server/www 
debug = false
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

## Banzai on Maven with Local Jar
* Clone this repository

```bash
$ git clone https://github.com/egehurturk/HttpServer.git
``` 

* Create a new project with Maven, from archetype (quickstart):
    * Enter `groupId`, `artifactId`, and packages for the new project  
```bash
$ mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```

* Change directory to the newly created maven project:
    * The name of the folder is simply the `groupId`, so enter the `groupId` you passed in the previous step for `<groupId>`
```bash
$ cd <groupId>
```

* Make a directory called `lib` to store `Banzai`'s JAR file:

```bash
$ mkdir lib
```

* Copy the JAR file from the original repository (the one you cloned from github) to `lib/`:

```bash
$ cp HttpServer/target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies ./lib/
```

* Open `pom.xml` in your favorite editor and create a repository:
    * This will allow Maven to look for Banzai's JAR inside `root/lib` 
```xml
<repositories>
	<repository>
		<id>localjar</id>
		<url>file://${project.basedir}/lib</url>
	</repository>
</repositories>
```

* Then add Banzai as a dependency:

```xml
<dependency>
    <groupId>com.egehurturk</groupId>
    <artifactId>BanzaiServer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency> 
```

* Add the `assembly` plugin for creating fat JAR's (i.e. JARs with dependencies):
```xml
<!-- Add this snippet under <pluginManagement> and <plugins> tags -->
<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <configuration>
        <archive>
            <manifest>
                <mainClass>org.example.App</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
</plugin>


<!-- Add this snippet under <build> and after <pluginManagement> -->
<plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <configuration>
          <source>8</source>
          <target>8</target>
      </configuration>
    </plugin>
    <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
          <executions>
              <execution>
                  <id>make-assembly</id> <!-- this is used for inheritance merges -->
                  <phase>package</phase> <!-- bind to the packaging phase -->
                  <goals>
                      <goal>single</goal>
                  </goals>
              </execution>
          </executions>
    </plugin>
</plugins>
```
 
* Open `src/main/java/<groupId>/App.java` and use the Banzai API!
    * Add the following lines

```java
package org.example;

import com.egehurturk.httpd.HttpServer;
import com.egehurturk.exceptions.*;
import com.egehurturk.handlers.*;
import com.egehurturk.handlers.FileResponse;
import com.egehurturk.handlers.Handler;
import com.egehurturk.handlers.JsonResponse;
import com.egehurturk.renderers.HTMLRenderer;
import com.egehurturk.util.ArgumentParser;
import com.egehurturk.util.HeaderEnum;
import com.egehurturk.util.MethodEnum;
import com.egehurturk.httpd.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException
    {
    	HttpServer s = new HttpServer(); 
		try {
			s.setConfigPropFile("YOUR_CONFIGURATION_PROPERTIES_FILE_HERE"); // enter a property configuration file
			s.configureServer();
		} catch (ConfigurationException err) {
			err.printStackTrace();
		}
		s.allowCustomUrlMapping(true);
		s.addHandler(MethodEnum.GET, "/bypass", new MyHandler());
		s.start();
	}

	static class MyHandler implements Handler {
		HttpResponse res = null;
		@Override
		public HttpResponse handle(HttpRequest req, HttpResponse res) {
			try {
				FileResponse fil = new FileResponse("HTML_FILE_HERE", res.getStream()); // enter a HTML file 
				res = fil.toHttpResponse();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return res;
		}
	}
}

```

* Build the project:
```bash
$ mvn package
```

* Run the JAR file:

```bash
$ java -jar target/*-jar-with-dependencies.jar
```

You should now have a running Http Server listening on the port you specified in `s.setConfigPropFile()` method
For more details on configuration, look at [Configuration](Configuration.md)

If you had trouble with adding Banzai as a dependency in Maven, I'd suggest you to look at this Medium article that explains including external JAR files to Maven through repositories: [Article](https://medium.com/@jakubtutko/maven-repository-inside-your-project-4c55b4d73be8)