# Build stage using JDK 25
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw mvnw
COPY pom.xml pom.xml

# Ensure the wrapper script has execute permissions and fetch dependencies
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy the source code and compile the application
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Add spring user instead of using root user
RUN groupadd --system spring && useradd --system --gid spring spring
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
