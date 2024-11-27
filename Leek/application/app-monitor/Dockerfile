FROM adoptopenjdk/openjdk11

ENV TZ=Asia/Shanghai

ADD target/applib /applib
COPY target/leek-monitor-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]