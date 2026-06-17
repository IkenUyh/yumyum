# Stage 1: Build file .jar bang Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Chay file .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
# Thêm tham số -Xms và -Xmx vào đây:
ENTRYPOINT ["java", "-Xms128m", "-Xmx300m", "-jar", "app.jar"]