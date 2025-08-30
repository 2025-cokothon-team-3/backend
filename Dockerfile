FROM amazoncorretto:17-alpine
WORKDIR /app
COPY app.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]