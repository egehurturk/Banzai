#!/bin/bash
cd banzai
mvn clean package
java -jar ./target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies.jar --config server.properties

# docker exec -ti  wizardly_tesla sh
# docker run -v $(PWD):/banzai -p 9091:9091 -it egeh/banzai:1.0-SNAPSHOT sh
# docker build -t egeh/banzai:1.0-SNAPSHOT .
