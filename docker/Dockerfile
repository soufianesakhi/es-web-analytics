FROM openjdk:14-jdk-alpine
VOLUME /tmp
COPY build/libs/es-web-analytics-*.jar /app/es-web-analytics.jar
WORKDIR /app
ENTRYPOINT exec java $JAVA_OPTS -jar /app/es-web-analytics.jar
