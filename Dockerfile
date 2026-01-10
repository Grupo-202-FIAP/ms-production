# ----- BUILD PHASE -----
FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ----- RUNTIME PHASE -----
FROM amazoncorretto:17-alpine

WORKDIR /app

# 🔹 ADICIONADO: Datadog Java Agent
ADD https://dtdg.co/latest-java-tracer /dd-java-agent.jar

COPY --from=build /app/target/app.jar app.jar

EXPOSE 8090

# 🔹 ADICIONADO: ativa Datadog sem mexer no ENTRYPOINT original
ENV JAVA_TOOL_OPTIONS="-javaagent:/dd-java-agent.jar"

# 🔹 ALTERAÇÃO MÍNIMA: apenas para ler SPRING_PROFILES_ACTIVE
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]