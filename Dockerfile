FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copiamos el pom.xml
COPY pom.xml .

# 2. Intentamos resolver, pero no nos detenemos si falla.
# Usamos -U para forzar la actualización de snapshots y librerías faltantes 
# ignorando cualquier caché de error previo.
RUN mvn dependency:resolve -B -U || echo "Ignorando caché de errores..."

# 3. Copiamos el código fuente
COPY src ./src

# 4. COMPILACIÓN FORZADA (-U)
# La bandera -U es la clave aquí: obliga a Maven a re-intentar la descarga 
# de iText ignorando el mensaje de "failure was cached".
RUN mvn package -DskipTests -B -U

# 5. Imagen final
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]