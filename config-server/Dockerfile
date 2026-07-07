FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# En Docker, el config-repo se monta como volumen (ver docker-compose.yml)
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
