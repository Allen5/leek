FROM adoptopenjdk/openjdk11

COPY target/leek-gateway-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]