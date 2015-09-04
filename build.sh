#! /bin/sh

mvn clean package
docker build -t dropwizard-oauth2-jwt-provider .

