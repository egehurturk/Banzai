FROM maven:3.6.3-jdk-11-slim

ARG server_port

RUN ls

COPY . ./banzai

ADD ./entrypoint.sh /entrypoint.sh

RUN chmod 0755 /entrypoint.sh

# This should change
EXPOSE $server_port

ENTRYPOINT /entrypoint.sh
