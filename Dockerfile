# TODO: TEMP FILE REMOVE WHEN FIX BOOT BUILD IMAGE

FROM openjdk:21-ea-slim

COPY app-standalone.jar .

ENTRYPOINT ["java", "-jar", "app-standalone.jar"]