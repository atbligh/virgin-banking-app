FROM maven:3.9.9-amazoncorretto-21 AS MAVEN_BUILD

EXPOSE 8080

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/banking-app-0.0.1-SNAPSHOT.jar /app/banking-app.jar

ENTRYPOINT ["java", "-jar", "banking-app.jar"]