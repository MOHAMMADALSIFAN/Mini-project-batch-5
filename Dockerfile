# First Stage - Build
FROM maven:3.9.9-eclipse-temurin-23 AS builder
ARG COMPILE_DIR=/compiledir
WORKDIR ${COMPILE_DIR}
# Copy necessary files for Maven
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src


# Set executable permission for mvnw
RUN chmod +x mvnw

# Run Maven to build the project
RUN ./mvnw clean package -DskipTests

# Second Stage - Runtime
FROM maven:3.9.9-eclipse-temurin-23
ARG WORK_DIR=/app
WORKDIR ${WORK_DIR}
COPY --from=builder /compiledir/target/miniprojectbatch5-0.0.1-SNAPSHOT.jar miniprojectbatch5.jar
ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "miniprojectbatch5.jar"]

# run
# docker build -t cihansifan/filename:v0.0.1 .  

# container
# docker run -p 8085:8080 cihansifan/filename:v0.0.1     

# to check redis  db using local  cmd
# redis-cli -u <public_url>