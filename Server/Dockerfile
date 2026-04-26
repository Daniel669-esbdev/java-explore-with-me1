FROM amazoncorretto:17
RUN yum install -y bash
COPY target/*.jar app.jar

COPY .github/workflows/wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

ENTRYPOINT ["./wait-for-it.sh", "stats-server:9090", "-t", "120", "--", "java", "-jar", "/app.jar"]