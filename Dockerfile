# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jdk-alpine

# Instalamos las fuentes necesarias para JasperReports
RUN apk add --no-cache fontconfig ttf-dejavu

WORKDIR /app
# CAMBIO AQUÍ: Agregamos /app/ antes de target
COPY --from=build /app/target/*.jar app.jar

# Modo Headless para JasperReports
ENV JAVA_OPTS="-Djava.awt.headless=true"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]