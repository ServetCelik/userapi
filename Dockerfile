FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]









## Use the desired base image
#FROM eclipse-temurin:17-jdk-alpine AS builder
#
## Set the working directory
#WORKDIR /app
#
## Copy the source code to the working directory
#COPY . .
#
## Build the JAR file
#RUN ./gradlew build
#
## Use a lightweight base image
#FROM eclipse-temurin:17-jdk-alpine
#
## Set the working directory
#WORKDIR /app
#
## Copy the JAR file from the builder stage
#COPY --from=builder /app/build/libs/*.jar app.jar
#
## Entrypoint command to run the JAR file
#ENTRYPOINT ["java", "-jar", "app.jar"]



## Step 1: Build the application with Gradle
#FROM gradle:8.5-alpine AS build
#
#COPY . .
#RUN gradle clean build -x test
#
## Step 2: Run jar file with Java
#FROM openjdk:21-jdk
#COPY --from=build /build/libs/userapi-0.0.1-SNAPSHOT.jar normal.jar
#COPY --from=build /build/libs/userapi-0.0.1-SNAPSHOT-plain.jar plain.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "normal.jar"]

#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#COPY build/libs/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

