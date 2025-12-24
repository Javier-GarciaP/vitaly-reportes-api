# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jdk-jammy

# Forzar usuario root para instalar paquetes de sistema
USER root

# Instalación de fuentes (Con limpieza de caché previa)
RUN apt-get update --fix-missing && \
    apt-get install -y --no-install-recommends libfontconfig1 ttf-dejavu && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /target/*.jar app.jar

# Configuración Headless para evitar errores de entorno gráfico
ENV JAVA_OPTS="-Djava.awt.headless=true"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]