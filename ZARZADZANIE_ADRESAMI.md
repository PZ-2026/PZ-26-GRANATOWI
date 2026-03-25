# Zarządzanie Adresami - Instrukcja

## Przegląd
Dodano funkcjonalność zarządzania adresami dla użytkowników i administratorów w aplikacji ArtSphere. Funkcja została zaimplementowana na podstawie projektu Galeriona.

## Nowe Funkcje

### Backend (Spring Boot)

#### 1. Tabela Bazy Danych
```sql
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    city VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house_number VARCHAR(20) NOT NULL,
    apartment_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### 2. REST API Endpoints

**Dla użytkowników (Kupujących i Artystów):**
- `GET /api/addresses/user/{userId}` - Pobierz wszystkie adresy użytkownika
- `GET /api/addresses/{id}` - Pobierz pojedynczy adres
- `POST /api/addresses` - Dodaj nowy adres
- `PUT /api/addresses/{id}` - Edytuj adres
- `DELETE /api/addresses/{id}` - Usuń adres

**Dla administratorów (pełne zarządzanie):**
- `GET /api/addresses/admin/all` - Pobierz wszystkie adresy wszystkich użytkowników
- `GET /api/addresses/admin/{id}` - Pobierz pojedynczy adres dowolnego użytkownika
- `PUT /api/addresses/admin/{id}` - Edytuj adres dowolnego użytkownika
- `DELETE /api/addresses/admin/{id}` - Usuń adres dowolnego użytkownika

#### 3. Zabezpieczenia
- Użytkownik może zarządzać tylko własnymi adresami
- System weryfikuje własność adresu przed operacjami UPDATE i DELETE
- Administrator ma pełny dostęp do wszystkich adresów

### Frontend (Android - Jetpack Compose)

#### 1. Nowe Ekrany

**AddressesScreen.kt** - Lista adresów
- Wyświetla wszystkie adresy użytkownika w formie kart
- Przycisk "Dodaj adres" (dla użytkowników i administratorów)
- Opcje edycji i usuwania dla każdego adresu
- **Administrator widzi**: Nazwę użytkownika (username) i ID obok każdego adresu
- **Administrator może**: Edytować i usuwać adresy wszystkich użytkowników

**AddressFormScreen.kt** - Formularz dodawania/edycji
- Pola: Miasto, Kod pocztowy, Ulica, Numer domu, Numer mieszkania
- **Dla administratora dodających**: Dodatkowe pole "ID użytkownika"
- Walidacja formularza
- Obsługa zarówno dodawania jak i edycji
- **Tryb administratora**: Używa endpoint administratora (`/admin/{id}`) dla edycji

#### 2. Nawigacja

**Dla Kupującego:**
1. Zaloguj się jako kupujący (buyer@gmail.pl / buyer123)
2. Kliknij ikonę profilu w prawym górnym rogu
3. Wybierz "Zarządzanie adresami"
4. Dodaj, edytuj lub usuń adresy

**Dla Administratora:**
1. Zaloguj się jako admin (admin@gmail.com / admin123)
2. Kliknij ikonę profilu w prawym górnym rogu
3. Wybierz "Zarządzanie adresami"
4. **Pełne zarządzanie wszystkimi adresami:**
   - Przeglądaj wszystkie adresy wszystkich użytkowników
   - Edytuj dowolny adres (kliknij ikonę ołówka)
   - Usuń dowolny adres (kliknij ikonę kosza)
   - Dodaj nowy adres dla dowolnego użytkownika (podając ID użytkownika)

## Struktura Plików

### Backend
```
backend/src/main/java/com/example/artsphere/
├── model/
│   └── Address.java              # Encja JPA
├── repository/
│   └── AddressRepository.java    # Repozytorium Spring Data
├── dto/
│   ├── AddressRequest.java       # DTO dla zapytań
│   └── AddressResponse.java      # DTO dla odpowiedzi
├── service/
│   └── AddressService.java       # Logika biznesowa
└── controller/
    └── AddressController.java    # Endpointy REST API

backend/src/main/resources/
└── schema.sql                     # Definicja tabeli addresses
```

### Frontend
```
frontend/app/src/main/java/com/example/artsphere/
├── api/
│   ├── ApiModels.kt              # Dodano AddressRequest i AddressResponse
│   ├── AddressApiService.kt      # Interfejs API Retrofit
│   └── RetrofitClient.kt         # Dodano addressApi
├── ui/screens/
│   ├── AddressesScreen.kt        # Ekran listy adresów
│   └── AddressFormScreen.kt      # Ekran formularza
└── ui/navigation/
    └── AppNavigation.kt          # Dodano routes dla adresów
```

## Zmiany w Istniejących Plikach

### Backend
- **schema.sql**: Dodano tabelę `addresses`

### Frontend
- **AppNavigation.kt**: 
  - Dodano `currentUserId` do stanu globalnego
  - Dodano routes: `addresses`, `addresses_admin`, `address_add`, `address_edit/{addressId}`
  - Zaktualizowano `onLoginSuccess` callback

- **LoginScreen.kt**:
  - Zaktualizowano callback do przekazywania `userId`

- **ClientPanelScreen.kt**:
  - Dodano parametr `onAddressesClick`
  - Podłączono przycisk "Zarządzanie adresami"

- **AdminPanelScreen.kt**:
  - Dodano parametr `onAddressesClick`
  - Podłączono przycisk "Zarządzanie adresami"

- **ApiModels.kt**:
  - Dodano `AddressRequest` i `AddressResponse`

- **RetrofitClient.kt**:
  - Dodano instancję `addressApi`

## Testowanie

### 1. Uruchom Backend
```bash
cd backend
.\mvnw.cmd spring-boot:run
```

### 2. Uruchom Frontend
Otwórz projekt w Android Studio i uruchom aplikację na emulatorze lub fizycznym urządzeniu.

### 3. Scenariusze Testowe

**Jako Kupujący:**
1. Zaloguj się (buyer@gmail.pl / buyer123)
2. Przejdź do panelu → Zarządzanie adresami
3. Dodaj nowy adres
4. Edytuj istniejący adres
5. Usuń adres

**Jako Administrator:**
1. Zaloguj się (admin@gmail.com / admin123)
2. Przejdź do panelu → Zarządzanie adresami
3. **Widok wszystkich adresów z nazwami użytkowników**
4. **Dodaj nowy adres:** Kliknij FAB → Wprowadź ID użytkownika → Wypełnij formularz
5. **Edytuj adres:** Kliknij ikonę ołówka → Modyfikuj dane
6. **Usuń adres:** Kliknij ikonę kosza → Potwierdź usunięcie
7. **Sprawdź użytkowników:** W panelu admina znajdziesz ID użytkowników

## Konta Testowe

| Email | Hasło | Rola |
|-------|-------|------|
| admin@gmail.com | admin123 | Administrator |
| artist@gmail.com | artist123 | Artysta/Sprzedawca |
| buyer@gmail.pl | buyer123 | Kupujący |

## Notatki Techniczne

1. **Pola opcjonalne**: `apartment_number` może być puste
2. **Bezpieczeństwo**: 
   - Każdy endpoint użytkownika weryfikuje własność adresu
   - Administratorzy mają pełny dostęp przez dedykowane endpointy `/admin/*`
3. **Kaskadowe usuwanie**: Usunięcie użytkownika automatycznie usuwa jego adresy
4. **Walidacja**: Wszystkie pola (oprócz `apartment_number`) są wymagane
5. **Format danych**: JSON przez REST API
6. **Wyświetlanie dla administratora**: Nazwa użytkownika i ID w każdej karcie adresu
7. **Dodawanie przez administratora**: Pole "ID użytkownika" w formularzu

## Różnice względem Galeriona

| Galeriona (Laravel/PHP) | ArtSphere (Spring Boot/Android) |
|------------------------|----------------------------------|
| Blade templates | Jetpack Compose |
| Route model binding | Manual findById() |
| Laravel validation | Service layer validation |
| `$fillable` array | JPA `@Column` annotations |
| Eloquent ORM | Spring Data JPA |

## Przyszłe Rozszerzenia (opcjonalne)

- [ ] Lista użytkowników dla administratora (dropdown zamiast ręcznego wpisywania ID)
- [ ] Dodanie flagi "domyślny adres"
- [ ] Wybór adresu podczas składania zamówienia
- [ ] Walidacja formatu kodu pocztowego (XX-XXX)
- [ ] Integracja z API weryfikacji adresów
- [ ] Historia zmian adresów
