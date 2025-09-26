FROM openjdk:17-jdk-slim
WORKDIR /app
COPY ./target/demo-0.0.1-SNAPSHOT.jar ./app.jar
COPY ./src/main/resources/ ./
CMD ["java","-jar","app.jar","--spring.profiles.active=dev"]
EXPOSE 8080