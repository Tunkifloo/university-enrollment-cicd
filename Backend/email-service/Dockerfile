FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /build

# 1. Copiar e instalar POM padre
COPY pom.xml ./pom.xml
RUN mvn install:install-file -Dfile=pom.xml -DpomFile=pom.xml -DgroupId=com.university -DartifactId=sistema-matriculas-parent -Dversion=1.0.0 -Dpackaging=pom

# 2. Construir common-lib
COPY Backend/common-lib/pom.xml ./Backend/common-lib/pom.xml
COPY Backend/common-lib/src ./Backend/common-lib/src
RUN mvn clean install -f Backend/common-lib/pom.xml -DskipTests

# 3. Construir email-service
COPY Backend/email-service/pom.xml ./Backend/email-service/pom.xml
RUN mvn dependency:go-offline -f Backend/email-service/pom.xml || true
COPY Backend/email-service/src ./Backend/email-service/src
RUN mvn clean package -DskipTests -f Backend/email-service/pom.xml

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/Backend/email-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]