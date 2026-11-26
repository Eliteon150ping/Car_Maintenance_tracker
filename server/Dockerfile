# Use Java 17 to build and run the application
FROM eclipse-temurin:17-jdk AS build

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

# Stage 2: Run the Spring Boot application
FROM eclipse-temurin:17-jre

# Create a non-root user for running the application
RUN useradd -m appuser

# Set the working directory inside the container
WORKDIR /app

# Create the uploads directory and assign ownership to appuser
RUN mkdir -p /app/uploads && chown -R appuser:appuser /app

# Copy the generated JAR from the build stage
COPY --from=build /app/target/sever-0.0.1-SNAPSHOT.jar app.jar

# Switch to the non-root user
USER appuser

# Run the generated JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

# Old docker file instructions
# Instruction	                   Purpose
# FROM	               Selects the Java 17 environment.
# WORKDIR	           Creates and selects /app inside the container.
# COPY	               Copies your Maven files and source code into the container.
# RUN	               Executes commands while building the image.
# ENTRYPOINT	       Defines the command that starts your application.

# New docker file instructions
# Instruction                                      Purpose
# FROM eclipse-temurin:17-jdk AS build             Uses Java 17 JDK to build the application and names this stage "build".
# WORKDIR /app                                     Sets the working directory inside the build container.
# COPY .mvn/ .mvn/                                 Copies the Maven Wrapper configuration into the container.
# COPY mvnw pom.xml ./                             Copies the Maven Wrapper script and project configuration.
# RUN chmod +x mvnw                                Gives the Maven Wrapper permission to execute.
# RUN ./mvnw dependency:go-offline                 Downloads the Maven dependencies before building.
# COPY src ./src                                   Copies the application source code into the container.
# RUN ./mvnw clean package -DskipTests             Builds the Spring Boot application into a JAR file.
# FROM eclipse-temurin:17-jre                      Starts a second stage using the smaller Java 17 runtime environment.
# WORKDIR /app                                     Sets the working directory inside the runtime container.
# COPY --from=build ... app.jar                    Copies the compiled JAR from the build stage into the runtime stage.
# ENTRYPOINT ["java", "-jar", "app.jar"]           Defines the command used to start the Spring Boot application.