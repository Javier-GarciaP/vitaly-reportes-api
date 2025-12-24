# Etapa 1: Construcción (Build)
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
# Copiar el pom.xml y descargar dependencias (para aprovechar el cache de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Instalar fuentes para JasperReports
RUN apk add --no-cache ttf-dejavu
# Copiar solo el JAR generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar
# Exponer el puerto
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]