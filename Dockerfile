FROM openjdk:17-slim

RUN adduser --system --group appuser
WORKDIR /app
COPY build/libs/*.jar app.jar
USER appuser

EXPOSE 8080 5005

ENTRYPOINT ["java", \
  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", \
  "-Xms2048m", \
  "-Xmx2048m", \
  "-jar", \
  "app.jar"]