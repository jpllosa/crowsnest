#FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
#WORKDIR /workspace/crowsnest
#
#COPY mvnw .
#COPY .mvn .mvn
#COPY pom.xml .
#COPY src/ src
#COPY docker-files/ app-files
#
#RUN mvn clean install

FROM eclipse-temurin:17-jdk-alpine
RUN addgroup -S crowsnest && adduser -S crowsnest -G crowsnest
USER crowsnest:crowsnest
COPY docker-files/ app-files
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} crowsnest.jar
ENTRYPOINT ["java", "-jar", "crowsnest.jar", "--spring.config.location=/app-files/application.properties"]