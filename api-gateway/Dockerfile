FROM amazoncorretto:17
RUN yum install -y bash
COPY target/*.jar app.jar

COPY .github/workflows/wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

ENTRYPOINT ["./wait-for-it.sh", "stats-db:5432", "-t", "120", "--", "java", "-jar", "/app.jar"]