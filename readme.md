Package and run the application:
`mvn clean package && java -jar target/dropwizard-oauth2-jwt-provider-1.0-SNAPSHOT.jar server src/main/resources/config.yml`

Then browse to [localhost:8080/ping](http://localhost:8080/ping) or `curl localhost:8080/ping`.
