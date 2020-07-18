FROM openjdk:14-jdk-alpine
COPY build/lib /app/lib/
COPY build/es-web-analytics-*-runner.jar /app/es-web-analytics.jar
WORKDIR /app
RUN mkdir /app/config
COPY .data/config /app/config
ENTRYPOINT exec java $JAVA_OPTS -jar /app/es-web-analytics.jar
