FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
# Instalamos fuentes básicas para que Jasper no de error con textos
RUN apk add --no-cache ttf-dejavu 
ENTRYPOINT ["java","-jar","/app.jar"]