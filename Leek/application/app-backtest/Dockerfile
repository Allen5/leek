FROM ghcr.io/graalvm/jdk:ol8-java17-22.3.3

COPY target/leek-backtest-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]