FROM adoptopenjdk/openjdk11

ENV TZ=Asia/Shanghai

ADD target/applib /applib
COPY target/leek-backtest-service.jar /application.jar

EXPOSE 8080

ENV JVM_OPTS="-xms256M -xmx512M"

ENTRYPOINT ["java", "-jar", "/application.jar", "${JVM_OPTS}"]