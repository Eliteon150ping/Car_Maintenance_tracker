# Use Java 17 to build and run the application
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy Maven Wrapper files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Give Maven Wrapper permission to execute
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy application source code
COPY src ./src

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Run the generated JAR file
ENTRYPOINT ["java", "-jar", "target/sever-0.0.1-SNAPSHOT.jar"]


# Instruction	                   Purpose
# FROM	               Selects the Java 17 environment.
# WORKDIR	           Creates and selects /app inside the container.
# COPY	               Copies your Maven files and source code into the container.
# RUN	               Executes commands while building the image.
# ENTRYPOINT	       Defines the command that starts your application.