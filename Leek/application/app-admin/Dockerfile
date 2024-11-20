FROM adoptopenjdk/openjdk11

COPY target/leek-admin-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]