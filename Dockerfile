#maven
FROM maven:3.9.9-eclipse-temurin-23-alpine AS maven
LABEL MAINTAINER="mindOf_L"

WORKDIR /build
COPY . /build
# Compile and package the application to an executable JAR
RUN mvn package -DskipTests

#java
FROM ghcr.io/graalvm/jdk-community:23 as backend
ENV JAVA_OPTS "-XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ARG JAR_FILE=ipreach-bot-j.jar

ENV APP_HOME /opt/app
WORKDIR $APP_HOME

# Copy the backend jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /build/target/${JAR_FILE} $APP_HOME

EXPOSE 8080 8081

ENTRYPOINT exec java $JAVA_OPTS -jar $APP_HOME/ipreach-backend.jar
