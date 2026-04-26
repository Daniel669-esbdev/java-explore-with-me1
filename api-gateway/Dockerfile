FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp

ARG JAR_FILE=target/server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar"]