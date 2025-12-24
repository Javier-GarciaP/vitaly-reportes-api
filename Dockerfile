# Etapa 1: Construcción (Build) con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jdk-jammy

# INSTALACIÓN DE FUENTES Y LIBRERÍAS GRÁFICAS Crítico para JasperReports
# libfontconfig1 y ttf-dejavu permiten que Jasper dibuje el PDF y use fuentes básicas
RUN apt-get update && apt-get install -y \
    libfontconfig1 \
    ttf-dejavu \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /target/*.jar app.jar

# Configuración para que Java no intente abrir ventanas gráficas (Modo Headless)
ENV JAVA_OPTS="-Djava.awt.headless=true"

EXPOSE 8080

# Ejecutamos con las JAVA_OPTS incluidas
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]