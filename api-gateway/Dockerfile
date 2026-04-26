FROM amazoncorretto:17
RUN yum install -y bash
COPY target/*.jar app.jar
ENTRYPOINT ["/wait-for-it.sh", "stats-server:9090", "-t", "120", "--", "java", "-jar", "/app.jar"]