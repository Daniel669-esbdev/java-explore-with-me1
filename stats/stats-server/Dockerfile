FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]