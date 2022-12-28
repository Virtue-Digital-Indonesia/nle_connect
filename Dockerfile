FROM openjdk:11-jre
LABEL author="Virtue Digital Indonesia"

# The application's jar file
ARG JAR_FILE

# Add the application's jar to the container
ADD ${JAR_FILE} nlebackend.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/nlebackend.jar"]
