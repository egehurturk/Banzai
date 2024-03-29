<!--
*** lahmacun ~ anonymous
***
***
*** To avoid retyping too much info. Do a search and replace for the following:
*** github_username, repo_name, twitter_handle, email, project_title, project_description
-->




<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![GitHub contributors](https://img.shields.io/github/contributors/egehurturk/HttpServer)](https://GitHub.com/egehurturk/HttpServer/graphs/contributors/)&nbsp;&nbsp;&nbsp;
[![Forks](https://img.shields.io/github/forks/egehurturk/HttpServer?style=plastic&color=green&label=Fork&maxAge=2592000)](https://GitHub.com/egehurturk/HttpServer/network/)
&nbsp;&nbsp;&nbsp;
[![Stargazers](https://img.shields.io/github/stars/egehurturk/HttpServer?style=plastic&color=green&label=Star&maxAge=2592000)](https://GitHub.com/egehurturk/HttpServer/stargazers/)
&nbsp;&nbsp;&nbsp;
[![Issues](https://img.shields.io/github/issues/egehurturk/HttpServer)](https://GitHub.com/egehurturk/HttpServer/issues/)
&nbsp;&nbsp;&nbsp;
![example workflow](https://github.com/egehurturk/banzai/actions/workflows/main.yml/badge.svg)


<!-- PROJECT LOGO

-->



<!-- TABLE OF CONTENTS
<details open="open">
  <summary><h2>Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#configuration">Installation</a></li>
        <li><a href="#deployment">Deployment</a></li>
      </ul>
    </li>
  </ol>
</details>
-->

<!-- PROJECT LOGO -->
<br>
<p align="center">

   <a href="https://github.com/egehurturk/Banzai">
    <img src="external/banzai.jpg" alt="Banzai Logo" width="200" height="200">
  </a>
  <h2 align="center">Banzai Server (A HTTP Server)</h3>

  <p align="center">
    A non-blocking, event-driven Http server from scratch, using plain Java. No additional dependencies (Take a look at  Built-with section) is needed.
    <br />
    <a href="https://github.com/egehurturk/Banzai/tree/main/docs"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/egehurturk/Banzai/issues">Report Bug</a>
    ·
    <a href="https://github.com/egehurturk/Banzai/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#configuration">Configuration</a></li>
        <li><a href="#deployment">Deployment</a></li>
      </ul>
    </li>
  </ol>
</details>





<!-- ABOUT THE PROJECT -->
## About The Project
Banzai is a light-weight, blazingly fast (when working on local instead of Docker), extendible web server. The server is capable of understanding & responding to HTTP/1.1 (not HTTP/2.0 or HTTPS protocols).


### Built With

* Java


<!-- GETTING STARTED -->
## Getting Started


These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


### Prerequisites

[Apache Maven](https://github.com/apache/maven) should be installed on your system and the `JAVA_HOME` environment variable should point to JDK home. Look at [this](https://maven.apache.org/install.html) to install maven. If you have Docker, you don't need to install Maven or Java.


### Installation for Docker
For more detailed instructions, see [Installation](docs/Installation%20and%20Quickstart.md)

> Note: do NOT use the maven wrapper commans (`mvnw` or `mvnw.cmd`) since there are some problems. I will solve this problem in v1.1.

1. Clone the repo
   ```sh
   $ git clone https://github.com/egehurturk/Banzai.git
   ```
2. Change permissions of starter bash file:
    ```sh
    $ cd Banzai
    $ chmod 755 run_docker.sh
    ```

3. Run the executable
   ```sh
   $ ./run_docker.sh -d "/path/to/config_file/"
   ```
   This will build up a Docker image and run the image.
   See [Docker](docs/Docker.md) for more details, or [Configuration](docs/Configuration.md) for details on configuring the server

### Installation on Maven
For more detailed instructions, see [Installation](docs/Installation%20and%20Quickstart.md)
1. Clone the repo
   ```sh
   $ git clone https://github.com/egehurturk/Banzai.git
   ```
2. Change directory to the folder:
    ```sh
    $ cd Banzai
    ```

3. Install the project to local maven repository:
   ```sh
   $ mvn install
   ```
   This will enable to use Banzai as a dependency in your `pom.xml`.
4. Add the Banzai dependency:
   ```xml
   <dependency>
      <groupId>com.egehurturk</groupId>
      <artifactId>BanzaiServer</artifactId>
      <version>1.0-SNAPSHOT</version>
   </dependency>
   ```
5.  Use the API (see [Installation - Quickstart](docs/Installation%20and%20Quickstart.md) and [User Guide](docs/User-Guide.md))
### Deployment
Banzai can be deployed in a Docker container. See [Docker](docs/Docker.md) for more information.

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/egehurturk/HttpServer/issues) for a list of proposed features (and known issues).

