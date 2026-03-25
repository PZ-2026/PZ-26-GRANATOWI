# 🎨 ArtSphere - Instrukcja uruchomienia

## 📋 Wymagania

- **Java 21** (JDK)
- **PostgreSQL** 
- **Android Studio** (dla frontendu)
- **Git**

## 🚀 Uruchomienie projektu

### 1. Sklonuj repozytorium
```bash
git clone <url-repozytorium>
cd PZ-26-GRANATOWI
```

### 2. Skonfiguruj bazę danych PostgreSQL

#### Utwórz bazę danych:
```sql
CREATE DATABASE artsphere_db;
```

#### Skonfiguruj użytkownika (opcjonalnie):
```sql
CREATE USER postgres WITH PASSWORD 'student';
GRANT ALL PRIVILEGES ON DATABASE artsphere_db TO postgres;
```

> **Uwaga:** Jeśli używasz innych danych logowania, zaktualizuj plik:
> `backend/src/main/resources/application.properties`

### 3. Uruchom backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run       # Linux/Mac
.\mvnw.cmd spring-boot:run   # Windows
```

Backend uruchomi się na: **http://localhost:8080**

#### Co się dzieje przy pierwszym uruchomieniu?
- ✅ Automatycznie tworzy tabele (schema.sql)
- ✅ Wstawia dane testowe z zahashowanymi hasłami (data.sql)
- ✅ Hasła są zabezpieczone BCrypt

### 4. Uruchom frontend (Android)

1. Otwórz folder `frontend` w **Android Studio**
2. Kliknij **Sync Project with Gradle Files**
3. Poczekaj aż pobiorą się zależności (Retrofit, OkHttp, etc.)
4. Uruchom aplikację na emulatorze lub urządzeniu

#### Konfiguracja URL backendu:

**Dla emulatora Android Studio:**
- Już skonfigurowane: `http://10.0.2.2:8080/`

**Dla prawdziwego urządzenia:**
1. Otwórz: `frontend/app/src/main/java/com/example/artsphere/api/RetrofitClient.kt`
2. Zmień `BASE_URL` na IP komputera w sieci lokalnej:
   ```kotlin
   private const val BASE_URL = "http://192.168.1.XXX:8080/"
   ```
3. Sprawdź IP komputera: `ipconfig` (Windows) lub `ifconfig` (Linux/Mac)

## 🔐 Dane testowe do logowania

| Rola | Email | Hasło |
|------|-------|-------|
| **Admin** | admin@gmail.com | admin123 |
| **Artysta** | artist@gmail.com | artist123 |
| **Kupujący** | buyer@gmail.pl | buyer123 |

## 🛠️ Rozwiązywanie problemów

### Backend nie startuje - błąd połączenia z bazą
```
org.postgresql.util.PSQLException: Connection refused
```
**Rozwiązanie:** Sprawdź czy PostgreSQL działa:
```bash
# Windows
services.msc -> PostgreSQL

# Linux/Mac
sudo systemctl status postgresql
```

### Android - błąd "Unable to connect"
**Rozwiązanie:** 
1. Sprawdź czy backend działa: otwórz http://localhost:8080/api/artworks
2. Sprawdź URL w `RetrofitClient.kt`
3. Upewnij się że telefon/emulator jest w tej samej sieci

### Błąd "Nieprawidłowy e-mail lub hasło"
**Rozwiązanie:**
- Upewnij się że używasz danych z tabeli powyżej
- Sprawdź czy `data.sql` został wykonany (hasła muszą być zahashowane)

## 📚 API Endpoints

### Autentykacja
- **POST** `/api/auth/login` - Logowanie
- **POST** `/api/auth/register` - Rejestracja

### Dzieła sztuki
- **GET** `/api/artworks` - Lista dzieł

### Admin (tylko do testów)
- **GET** `/api/admin/check-passwords` - Sprawdź status haseł
- **POST** `/api/admin/hash-passwords` - Zahashuj hasła (jednorazowo)

## 🎯 Technologie

**Backend:**
- Spring Boot 3.5.12
- Spring Security (BCrypt)
- PostgreSQL
- JPA/Hibernate

**Frontend:**
- Android (Kotlin)
- Jetpack Compose
- Retrofit 2.9.0
- Material Design 3

## ✅ Checklist pierwszego uruchomienia

- [ ] PostgreSQL zainstalowany i działa
- [ ] Baza danych `artsphere_db` utworzona
- [ ] Java 21 zainstalowana
- [ ] Backend uruchomiony (port 8080)
- [ ] Android Studio otwarte z projektem frontend
- [ ] Gradle sync zakończony
- [ ] Aplikacja uruchomiona na emulatorze/urządzeniu
- [ ] Logowanie działa z danymi testowymi

## 🎉 Gotowe!

Jeśli wszystko działa:
1. Zaloguj się w aplikacji Android
2. Sprawdź listę dzieł sztuki
3. Testuj rejestrację nowych użytkowników

---

**Problemy?** Sprawdź logi backendu w konsoli lub logi Logcat w Android Studio.
