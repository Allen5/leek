FROM adoptopenjdk/openjdk11

ADD target/applib /applib
COPY target/leek-etl-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]