FROM eclipse-temurin:21

ENV SPRING_PROFILE="live"
ENV SERVER_PORT=8000

RUN mkdir /opt/app
COPY target/gateway.jar /opt/app
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-Dserver.port=${SERVER_PORT}","-jar", "/opt/app/gateway.jar"]
