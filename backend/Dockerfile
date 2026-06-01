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

# Ustawiamy zmienne środowiskowe
ENV JWT_SECRET=q7Q9mZr2kH8vA1pXn5sT4dY6cL0wJ3uGf9bN2xR8eM6tV1aZy7P4hK9sW2jD0cQx
ENV JWT_ACCESS_TTL_MINUTES=15
ENV JWT_REFRESH_TTL_DAYS=7

# Informujemy, że aplikacja działa na porcie 8080
EXPOSE 8080

# Komenda uruchamiająca Spring Boota
ENTRYPOINT ["java", "-jar", "app.jar"]