<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Thanks again! Now go create something AMAZING! :D
***
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
[![Forks](https://img.shields.io/github/forks/egehurturk/HttpServer?style=social&label=Fork&maxAge=2592000)](https://GitHub.com/egehurturk/HttpServer/network/)
&nbsp;&nbsp;&nbsp;
[![Stargazers](https://img.shields.io/github/stars/egehurturk/HttpServer?style=social&label=Star&maxAge=2592000)](https://GitHub.com/egehurturk/HttpServer/stargazers/)
&nbsp;&nbsp;&nbsp;
[![Issues](https://img.shields.io/github/issues/egehurturk/HttpServer)](https://GitHub.com/egehurturk/HttpServer/issues/)
&nbsp;&nbsp;&nbsp;



<!-- PROJECT LOGO
<br>
<p align="center">

   <a href="https://github.com/egehurturk/HttpServer">
    <img src="external/banzai.jpg" alt="Banzai Logo" width="200" height="200">
  </a>
  
  <h2 align="center">Banzai Server (A HTTP Server)</h3>

  <p align="center">
    A non-blocking, event-driven Http server from scratch, using plain Java. No additional dependencies (Take a look at  Built-with section) is needed. 
    <br />
    <a href="https://github.com/egehurturk/HttpServer"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/egehurturk/HttpServer/issues">Report Bug</a>
    ·
    <a href="https://github.com/egehurturk/HttpServer/issues">Request Feature</a>
  </p>
</p>
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
<br />
<p align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Best-README-Template</h3>

  <p align="center">
    An awesome README template to jumpstart your projects!
    <br />
    <a href="https://github.com/othneildrew/Best-README-Template"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/othneildrew/Best-README-Template">View Demo</a>
    ·
    <a href="https://github.com/othneildrew/Best-README-Template/issues">Report Bug</a>
    ·
    <a href="https://github.com/othneildrew/Best-README-Template/issues">Request Feature</a>
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
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>





<!-- ABOUT THE PROJECT -->
## About The Project
Understanding HTTP in web development is essential. To strengthen my Java and web skills (also a school project), I decided to create my own http server
which is capable of parsing, writing, logging, etc. 

### Built With

* [Apache Maven](https://github.com/apache/maven)
* [Apache Logging-Log4J2](https://github.com/apache/logging-log4j2)
* [JUnit](https://github.com/junit-team/junit4)


<!-- GETTING STARTED -->
## Getting Started


These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


### Prerequisites

[Apache Maven](https://github.com/apache/maven) should be installed on your system and the `JAVA_HOME` environment variable should point to JDK home. Look at [this](https://maven.apache.org/install.html) to install maven


### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/egehurturk/HttpServer.git
   ```
2. Change permissions of starter bash file:
    ```sh
    chmod +x banzai.sh
    ```

3. Run the server
   ```sh
   ./banzai --port 9090 --host 0.0.0.0 --backlog 50 --name Banzai --webroot www
   ```
   OR
   ```sh
   sh banzai.sh --port 9090 --host 0.0.0.0 --backlog 50 --name Banzai --webroot www
   ```
   See [#configuration] for details on configuring the server
   
4. Optional linking \
  You can link the executable (see 2nd step) to use `banzai` executable without being in the installed directory
  Create a symbolic link
   ```sh
   sudo ln -s <banzai.sh_path> /usr/local/bin/banzai
   ```
   And then use `banzai` as a program:
   ```sh
   banzai --config server.properties
   ```
   Note: you don't need to be in any path, `banzai.sh` will automatically discover which path you are in and then call the `jar` file and will look for `server.properties` automatically.
   
   
### Configuration
Configuration can happen in two ways. One is to pass in CLA (Command Line Arguments) while executing or running the server:

1. CLA (Command Line Arguments)
  ```sh
  banzai run http -p 9090 -h 127.0.0.1 -b 50 -n AwesomeServer -w www
  ```
  This creates a server running on port `9090`, host `127.0.0.1`, a backlog of `50`, a name of `AwesomeServer`, and determines web root as `/www`
  
  All command line arguments:
  
  1  `--port` | `-p`: port numer that the server is running on\
  2 `--host` | `-h`: host\
  3 `--backlog` | `-b`: backlog (maximum number of Http threads in queue)\
  4 `--name` | `-n`: name of the server\
  5 `--webroot` | `-w`: web root of server\

  
2. Using a `properties` file:
   ```sh
    ./banzai --config server.properties
   ```
  One can also configure the server with using a `properties` file located in `root/src/main/resources/server.properties` directory. Every
  argument is passed as a key=value pair. Server reads all keys and values from the file and sets values accordingly. Same arguments are present.

### Deployment
Deployment on docker. 

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/egehurturk/HttpServer/issues) for a list of proposed features (and known issues).

