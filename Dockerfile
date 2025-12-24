# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Alpine es mucho más ligero y estable)
FROM eclipse-temurin:21-jdk-alpine

# Instalamos las fuentes necesarias usando 'apk' (gestor de Alpine)
# Estas librerías son las equivalentes para que JasperReports funcione
RUN apk add --no-cache fontconfig ttf-dejavu

WORKDIR /app
COPY --from=build /target/*.jar app.jar

# Modo Headless para JasperReports
ENV JAVA_OPTS="-Djava.awt.headless=true"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]