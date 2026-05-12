FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
ARG JAR_FILE=stats/stats-server/target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]