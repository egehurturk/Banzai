# Installing Banzai & Quickstart

- [Prerequisits](#prerequisits)
- [Installing and Using Banzai](#installing-banzai)
  * [Banzai on Docker](#banzai-on-docker)
    * [Banzai on Virtual Machine with Docker](#banzai-on-virtual-machine-with-docker)
  * [Banzai on Maven with Local Jar - Option 1](#banzai-on-maven-with-local-jar-option-1)
  * [Banzai on Maven with Local Jar - Option 2 (Deprecated, DO NOT USE)](#banzai-on-maven-with-local-jar-option-2)
  

## Prerequisits
You'll need:
* Java 
* Maven 
* Have `JAVA_HOME` pointed to `JDK` home path
* Docker


## Banzai on Docker
* Clone this repository 

```bash
$ git clone https://github.com/egehurturk/Banzai.git
``` 


* `cd`'to the directory you cloned 

```bash
$ cd Banzai 
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
$ cd .. && cd .. && cd Banzai
$ ./run_docker.sh -d "/Users/$USER/test_server"
``` 

These steps will create a Docker container and run the server inside the docker container. Any changes you made to the directory you want to serve, `test_server` in this case, will be updated on the container.

You can skip the "clean build" for the project with inputting "N" for the prompt. 

### Banzai on Virtual Machine with Docker
The installation steps are the same. However, you can not directly access to the `localhost` of Virtual Machine.
You should forward the port you started `Banzai` on the guest machine, and forward that port to another port in the host machine

* If you are using `VirtualBox` (highly recommended), then press the Settings icon
* Go to the network tab
* Click `Advanced`
* Click "Port Forwarding"
* Add one (click to the first green button on the margin of the screen)
* Enter "web" for name, `127.0.0.1` for Host IP, any port for Host Port, IP of your virtual machine to Guest IP, and the port you chosed in `server.properties` to Guest Port
* Start `Banzai` on virtual machine:
```bash
$ ./run_docker.sh -d "<path_to_your_project>"
```
* Then go to `localhost:<port_you_entered_in_Host_Port>` 
* You'll see `Banzai` running on the port


## Banzai on Maven with Local Jar Option 1
* Clone this repository

```bash
$ git clone https://github.com/egehurturk/Banzai.git
``` 

* Create a new project with Maven, from archetype (quickstart):
    * Enter `groupId`, `artifactId`, and packages for the new project  
```bash
$ mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```

* Change directory to Banzai's folder:

```bash
$ cd Banzai
```

* Install the project to local Maven repository 

```bash
$ mvn install
```

* Then `cd` back to the project you created with `mvn`

```bash
$ cd <artifactId> # your artifactId here
```

* Open up `pom.xml` in your favorite editor and add the following dependency:
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
* Open `src/main/java/<artifactId>/App.java` and use the Banzai API!
    * Add the following lines

```java
package <groupId> // enter your groupId

import com.egehurturk.httpd.HttpServer;
import com.egehurturk.exceptions.*;
import com.egehurturk.handlers.FileResponse;
import com.egehurturk.handlers.Handler;
import com.egehurturk.util.Methods;
import com.egehurturk.httpd.*;

import java.io.FileNotFoundException;

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
	try
	{
		s.setConfigPropFile("YOUR_CONFIGURATION_PROPERTIES_FILE_HERE"); // enter a property configuration file
		s.configureServer();
	} catch (ConfigurationException err)
	{
		err.printStackTrace();
	}
	s.allowCustomUrlMapping(true);
	s.addHandler(Methods.GET, "/helloworld", new MyHandler());
	s.start();
    }

	static class MyHandler implements Handler 
	{
		HttpResponse res = null;
		@Override
		public HttpResponse handle(HttpRequest req, HttpResponse res) 
		{
			try 
			{
				FileResponse fil = new FileResponse("HTML_FILE_HERE", res.getStream()); // enter a HTML file 
				res = fil.toHttpResponse();
			} catch (FileNotFoundException e)
			{
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

## Banzai on Maven with Local Jar Option 2

|  :warning: *Do not prefer installing Banzai with this option, always use [Banzai on Maven with Local Jar - Option 1](#banzai-on-maven-with-local-jar-option-1) to install Banzai with local JARs* |
| --- |

* Clone this repository

```bash
$ git clone https://github.com/egehurturk/Banzai.git
``` 

* Create a new project with Maven, from archetype (quickstart):
    * Enter `groupId`, `artifactId`, and packages for the new project  
```bash
$ mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```

* Change directory to the newly created maven project:
    * The name of the folder is simply the `artifactId`, so enter the `artifactId` you passed in the previous step for `<artifactId>`
```bash
$ cd <articactId>
```

* Make a directory called `lib` to store `Banzai`'s JAR file:

```bash
$ mkdir lib
```

* Copy the JAR file from the original repository (the one you cloned from github) to `lib/`:

```bash
$ cp Banzai/target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies ./lib/
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
 
* Open `src/main/java/<artifactId>/App.java` and use the Banzai API!
    * Add the following lines

```java
package org.example;

import com.egehurturk.httpd.HttpServer;
import com.egehurturk.exceptions.*;
import com.egehurturk.handlers.FileResponse;
import com.egehurturk.handlers.Handler;
import com.egehurturk.util.Methods;
import com.egehurturk.httpd.*;

import java.io.FileNotFoundException;

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
		s.addHandler(Methods.GET, "/bypass", new MyHandler());
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

