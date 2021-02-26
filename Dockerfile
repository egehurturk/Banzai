FROM maven:3.6.3-jdk-11-slim as build

ADD ./entrypoint.sh /entrypoint.sh
RUN chmod 0755 /entrypoint.sh
# This should change
EXPOSE 9091
ENTRYPOINT /entrypoint.sh
