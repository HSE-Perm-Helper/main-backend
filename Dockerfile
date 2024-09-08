FROM openjdk:17-jdk-slim

COPY app-standalone.jar .

ENTRYPOINT ["java", "-jar", "app-standalone.jar"]