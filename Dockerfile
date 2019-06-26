FROM openjdk:11-jdk-slim

COPY ./build/libs/*.jar /usr/src/myapp/app.jar
WORKDIR /usr/src/myapp

CMD ["java", "-jar", "app.jar"]