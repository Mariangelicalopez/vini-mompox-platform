# --- ETAPA DE CONSTRUCCIÓN (BUILD STAGE) ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA DE EJECUCIÓN (RUNTIME STAGE) ---
FROM eclipse-temurin:17-jre-alpine 
LABEL maintainer="ardelvillar@hotmail.com"
EXPOSE 8081
WORKDIR /app
COPY --from=build /app/target/products-service-0.0.1-SNAPSHOT.jar products-service.jar
ENTRYPOINT ["java", "-jar", "products-service.jar"]