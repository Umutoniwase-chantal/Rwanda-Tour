
FROM maven:3.8.3-openjdk-17 as build


COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim


COPY --from=build /target/spring-web-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 1099

ENTRYPOINT ["java", "-jar", "app.jar"]
