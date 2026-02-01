FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew buildFatJar

FROM musescore:latest

WORKDIR /app

# Switch to root to install packages
USER root

RUN apt-get update && apt-get install -y \
    openjdk-21-jre-headless \
    libfreetype6 \
    fontconfig \
    && rm -rf /var/lib/apt/lists/*

USER ubuntu 

COPY --from=build /app/build/libs/app.jar app.jar

ENV MUSESCRE_EXECUTABLE="musescore"

ENTRYPOINT ["java", "-jar", "app.jar"]
