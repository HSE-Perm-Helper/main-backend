FROM openjdk:21-ea-slim

COPY app-standalone.jar .

ENTRYPOINT ["java", "-jar", "app-standalone.jar"]