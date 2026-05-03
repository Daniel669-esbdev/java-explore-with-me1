FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]