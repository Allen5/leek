FROM adoptopenjdk/openjdk11

COPY target/leek-monitor-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar", "--spring.profiles.active=docker"]