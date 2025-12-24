# 1. Usar una imagen de Maven completa
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 2. Instalar curl (necesario para descargar la librería manualmente)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 3. Copiar solo el POM primero
COPY pom.xml .

# 4. TRUCO MAESTRO: Descarga manual e instalación en el repo local
# Esto evita que Maven intente buscar el .pom inexistente en Jaspersoft
RUN curl -L -o itext-2.1.7.js10.jar https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-releases/com/lowagie/itext/2.1.7.js10/itext-2.1.7.js10.jar && \
    mvn install:install-file \
    -Dfile=itext-2.1.7.js10.jar \
    -DgroupId=com.lowagie \
    -DartifactId=itext \
    -Dversion=2.1.7.js10 \
    -Dpackaging=jar \
    -B

# 5. Pre-descargar el resto de dependencias (ignorando errores menores)
RUN mvn dependency:go-offline -B || echo "Ignorando errores de red parciales..."

# 6. Copiar código fuente y compilar
COPY src ./src
RUN mvn package -DskipTests -B

# 7. Imagen de ejecución liviana
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render usa el puerto 8080 por defecto, pero es configurable
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]