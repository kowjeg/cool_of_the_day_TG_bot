FROM openjdk:23-jdk-slim

WORKDIR /app

COPY target/TGBotCoolGuyOfTheDay-1.0-SNAPSHOT.jar /app/TGBotCoolGuyOfTheDay.jar

ENTRYPOINT ["java", "-jar", "TGBotCoolGuyOfTheDay.jar"]