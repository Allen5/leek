FROM mcr.microsoft.com/playwright/java:v1.48.0-noble

ENV TZ=Asia/Shanghai

ADD target/applib /applib
COPY target/leek-creep-service.jar /application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/application.jar"]