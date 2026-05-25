# ETAP 1: Budowanie aplikacji przy użyciu Mavena i Javy 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Kopiujemy pliki projektu (w tym pom.xml i kod źródłowy)
COPY . .

# Kompilujemy projekt i tworzymy plik .jar (pomijamy testy, żeby było szybciej)
RUN mvn clean package -DskipTests

# ETAP 2: Uruchomienie aplikacji (czyste, lekkie środowisko uruchomieniowe Javy 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy tylko gotowy plik .jar z poprzedniego etapu
COPY --from=build /app/target/*.jar app.jar

# Informujemy, że aplikacja działa na porcie 8080
EXPOSE 8080

# Komenda uruchamiająca Spring Boota
ENTRYPOINT ["java", "-jar", "app.jar"]