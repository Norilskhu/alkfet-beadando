FROM eclipse-temurin:17-jdk-alpine AS build
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
COPY --from=build /app/target/*.jar app.jar
WORKDIR /app
FROM eclipse-temurin:17-jre-alpine

RUN ./mvnw package -DskipTests -B
COPY src src
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY .mvn .mvn
COPY mvnw pom.xml ./
WORKDIR /app

