# action-monitor proof of concept

## How to build and deploy:

The project is using java11 and maven.

For building the project just run:

``` 
mvn clean install 
```

In order to execute the program the following command can be run:

``` 
mvn spring-boot:run
```

Also can be run by going to the target folder and running

```
java -jar action-monitor-0.0.1-SNAPSHOT.jar
```

This will start the application in port 8080.

## Check application status

Once the application is running go to

(status endpoint)[http://localhost:8080/actuator/health]

Spring boot start actuator has been used for this endpoint

## Check application version

Once the application is running go to

[info endpoint](http://localhost:8080/actuator/info)

Spring boot start actuator has been used for this endpoint

## Check database content

The application uses an embedded h2 database. To check the content of the db, once the application is running, go to:

[db content](http://localhost:8080/h2-console)

and use the jdbc url that can be found in the logs when starting the app. For instance:
jdbc:h2:mem:9e1f6e61-3f56-43e3-9ad8-51c607028726

## Testing strategy

In order to write the unit test, a mock strategy has been used for the dependencies in the classes. 