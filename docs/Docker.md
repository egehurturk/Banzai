Using Banzai on Docker is straightforward. However, with this approach, you cannot use any other components of Banzai or extend Banzai's functionality with its API.

> You can only use Banzai as a static web server using Docker, i.e., Banzai will serve documents and you can access the documents by entering the document name with extension in URL. For example, if `hey.html` exists under web root and if the web root is configured in `server.properties`, then Banzai will serve this document under `localhost:port/hey.html`. This means that you cannot map certain URLs to documents, or use Banzai's API. 


The installation step is discussed in  <a href="Installation%20and%20Quickstart.md#banzai-on-docker">Installation and Quickstart | Banzai on Docker</a> 

<!--[Installation and Quickstart](Installation%20and%20Quickstart.md#Banzai) -->


