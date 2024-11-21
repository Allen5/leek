FROM adoptopenjdk/openjdk11

COPY target/leek-backtest-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]