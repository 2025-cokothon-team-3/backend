FROM gradle:8-jdk17 AS build
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

RUN gradle dependencies --no-daemon

COPY src src
RUN gradle bootJar --no-daemon

FROM amazoncorretto:17-alpine
WORKDIR /app

# 불필요한 패키지 설치 방지
RUN apk add --no-cache tzdata

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]