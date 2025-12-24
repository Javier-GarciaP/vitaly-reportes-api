# Estructura típica para Spring Boot con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copiar el pom.xml
COPY pom.xml .

# 2. SOLUCIÓN AL ERROR:
# En lugar de go-offline, usamos resolve y resolve-plugins.
# Añadimos "|| true" para que si el servidor de Jaspersoft da error 409, 
# el build NO se detenga y continúe con el siguiente paso.
RUN mvn dependency:resolve-plugins dependency:resolve -B || echo "Ignorando errores de resolución parcial..."

# 3. Copiar el código fuente
COPY src ./src

# 4. Compilar el proyecto
# Aquí Maven intentará bajar lo que falte (como iText) de forma definitiva.
# Usamos -o (offline) opcionalmente si estamos seguros de tener todo, 
# pero es mejor dejarlo normal para que asegure la descarga de iText.
RUN mvn package -DskipTests -B

# 5. Imagen final de ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]