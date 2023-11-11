FROM gradle:latest AS BUILD
WORKDIR /app
COPY . .
RUN gradle build -PmyName=HsePermHelper.jar

# Package stage

FROM openjdk:latest
ENV JAR_NAME=HsePermHelper.jar
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 8080
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME