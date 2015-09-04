FROM java:8-jre

MAINTAINER Remmelt Pit <remmelt@gmail.com>

RUN mkdir /service
COPY target/srv.jar /service/srv.jar
COPY target/classes/config.yml /service/config.yml

EXPOSE 8080
ENTRYPOINT java -jar /service/srv.jar server /service/config.yml
