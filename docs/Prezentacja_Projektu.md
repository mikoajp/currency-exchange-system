# Prezentacja Projektu
## System Mobilny Kantoru Wymiany Walut

**Przedmiot:** Zagadnienia sieciowe w systemach mobilnych  
**Rok akademicki:** 2024/2025  
**Autorzy:** Mikołaj Przybysz, Jakub Dyba  
**Data:** 26 stycznia 2026

---

## 📋 Slajd 1: Strona Tytułowa

```
╔════════════════════════════════════════════════╗
║                                                ║
║   SYSTEM MOBILNY KANTORU WYMIANY WALUT        ║
║                                                ║
║   Zagadnienia sieciowe w systemach mobilnych  ║
║                                                ║
║   Mikołaj Przybysz                            ║
║   Jakub Dyba                                  ║
║                                                ║
║   Rok akademicki 2024/2025                    ║
║                                                ║
╚════════════════════════════════════════════════╝
```

---

## 📋 Slajd 2: Agenda Prezentacji

### Plan Prezentacji (15 min)

1. **Cel i zakres projektu** (2 min)
2. **Architektura systemu** (3 min)
3. **Technologie i narzędzia** (2 min)
4. **Demonstracja działania** (5 min)
5. **Statystyki i testy** (2 min)
6. **Podsumowanie i wnioski** (1 min)

---

## 📋 Slajd 3: Cel Projektu

### 🎯 Główny Cel

Stworzenie **kompletnego systemu mobilnego** umożliwiającego:

✅ Wymianę walut w czasie rzeczywistym  
✅ Zarządzanie wirtualnym portfelem  
✅ Przeglądanie aktualnych kursów NBP  
✅ Bezpieczne transakcje finansowe  

### 🎓 Cele Edukacyjne

- Praktyczne zastosowanie komunikacji **REST API**
- Integracja z zewnętrznym API (**NBP**)
- Implementacja **JWT Authentication**
- Architektura **trójwarstwowa** (Mobile-Backend-Database)
- Testowanie aplikacji mobilnej i backendowej

---

## 📋 Slajd 4: Zakres Funkcjonalny

### ✨ Funkcjonalności Zrealizowane

#### 👤 Moduł Użytkownika
- ✅ Rejestracja z walidacją (email, hasło min. 6 znaków)
- ✅ Logowanie z tokenem JWT
- ✅ Bezpieczne przechowywanie hasła (BCrypt)

#### 💰 Moduł Portfela
- ✅ Wyświetlanie sald w 5 walutach (PLN, USD, EUR, GBP, CHF)
- ✅ Doładowanie konta PLN (symulacja PayPal)
- ✅ Automatyczne tworzenie portfeli dla nowych użytkowników

#### 💱 Moduł Wymiany
- ✅ Wymiana walut według kursów NBP
- ✅ Walidacja salda przed transakcją
- ✅ Atomowe transakcje (ACID)
- ✅ Historia wszystkich operacji

#### 📊 Moduł Kursów
- ✅ Pobieranie kursów z API NBP
- ✅ Cache kursów w bazie danych
- ✅ Automatyczna aktualizacja (scheduler - codziennie 12:00)
- ✅ Obsługa kursów historycznych

---

## 📋 Slajd 5: Architektura Systemu

### 🏗️ Architektura Trójwarstwowa

```
┌─────────────────────────────────────────────┐
│         APLIKACJA MOBILNA                   │
│         (React Native)                      │
│  ┌─────────────────────────────────────┐   │
│  │ LoginScreen    RegisterScreen       │   │
│  │ WalletScreen   ExchangeScreen       │   │
│  │ TopUpScreen    HistoryScreen        │   │
│  └─────────────────────────────────────┘   │
│           ↕ REST API (HTTPS/JWT)           │
└─────────────────────────────────────────────┘
                    ↕
┌─────────────────────────────────────────────┐
│         BACKEND (Spring Boot)               │
│  ┌─────────────────────────────────────┐   │
│  │ Controllers (REST API Endpoints)    │   │
│  │ Services (Business Logic)           │   │
│  │ Security (JWT + BCrypt)             │   │
│  │ Scheduler (Auto-update rates)       │   │
│  └─────────────────────────────────────┘   │
│           ↕ JPA/Hibernate                  │
└─────────────────────────────────────────────┘
                    ↕
┌─────────────────────────────────────────────┐
│      BAZA DANYCH (PostgreSQL)               │
│  ┌─────────────────────────────────────┐   │
│  │ users          wallets              │   │
│  │ transactions   exchange_rates       │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
                    ↕
┌─────────────────────────────────────────────┐
│      ZEWNĘTRZNE API (NBP)                   │
│      http://api.nbp.pl/api/                │
└─────────────────────────────────────────────┘
```

---

## 📋 Slajd 6: Technologie - Backend

### ☕ Backend Stack

| Technologia | Wersja | Zastosowanie |
|------------|--------|--------------|
| **Java** | 17 | Język programowania |
| **Spring Boot** | 3.2.x | Framework aplikacyjny |
| **Spring Security** | 6.x | Autoryzacja i bezpieczeństwo |
| **Spring Data JPA** | 3.2.x | Warstwa dostępu do danych |
| **PostgreSQL** | 16 | Baza danych relacyjna |
| **Flyway** | 10.x | Migracje bazy danych |
| **JWT (jjwt)** | 0.12.x | Tokeny uwierzytelniania |
| **Lombok** | 1.18.x | Redukcja boilerplate code |
| **Gradle** | 8.5 | Build tool |
| **JUnit 5** | 5.10.x | Framework testowy |
| **Docker** | 24.x | Konteneryzacja |

### 🔑 Kluczowe Biblioteki
- **RestTemplate** - Komunikacja z API NBP
- **BCryptPasswordEncoder** - Hashowanie haseł
- **Jackson** - Serializacja JSON
- **Swagger/OpenAPI** - Dokumentacja API

---

## 📋 Slajd 7: Technologie - Mobile

### 📱 Mobile Stack

| Technologia | Wersja | Zastosowanie |
|------------|--------|--------------|
| **React Native** | 0.73.x | Framework mobilny |
| **TypeScript** | 5.0.x | Język programowania |
| **React Navigation** | 6.x | Nawigacja między ekranami |
| **React Query** | 5.x | Cache i zarządzanie stanem |
| **Axios** | 1.6.x | HTTP Client |
| **AsyncStorage** | 1.24.x | Lokalne przechowywanie danych |
| **React Hook Form** | 7.x | Obsługa formularzy |
| **Jest** | 29.x | Framework testowy |
| **Testing Library** | 13.x | Testy komponentów |

### 🎨 UI/UX
- Native Android Components
- Material Design principles
- Pull-to-refresh
- Loading states & error handling

---

## 📋 Slajd 8: Model Bazy Danych

### 🗄️ Schemat ERD

```sql
┌──────────────────┐
│     users        │
├──────────────────┤
│ id (PK)          │
│ email (UK)       │
│ password         │
│ name             │
│ role             │
│ created_at       │
└──────────────────┘
        │ 1
        │
        │ *
┌──────────────────┐      ┌──────────────────┐
│    wallets       │      │  exchange_rates  │
├──────────────────┤      ├──────────────────┤
│ id (PK)          │      │ id (PK)          │
│ user_id (FK)     │      │ currency         │
│ currency         │      │ rate             │
│ balance          │      │ effective_date   │
│ created_at       │      │ fetched_at       │
└──────────────────┘      └──────────────────┘
        │ 1
        │
        │ *
┌──────────────────┐
│  transactions    │
├──────────────────┤
│ id (PK)          │
│ user_id (FK)     │
│ type             │ ← TOP_UP, EXCHANGE
│ from_currency    │
│ to_currency      │
│ amount           │
│ rate             │
│ status           │ ← COMPLETED, PENDING, FAILED
│ created_at       │
└──────────────────┘
```

### 🔒 Constraints
- Foreign Keys z CASCADE
- Unique constraint na email
- NOT NULL na kluczowych polach
- Decimal(19,4) dla precyzyjnych kwot

---

## 📋 Slajd 9: Integracja z API NBP

### 🏦 Narodowy Bank Polski API

**Endpoint:** `http://api.nbp.pl/api/exchangerates/rates/A/{currency}/`

#### Przykład Response:
```json
{
  "table": "A",
  "currency": "dolar amerykański",
  "code": "USD",
  "rates": [
    {
      "no": "015/A/NBP/2026",
      "effectiveDate": "2026-01-26",
      "mid": 4.0123
    }
  ]
}
```

#### Obsługiwane Waluty:
- **USD** - Dolar amerykański
- **EUR** - Euro
- **GBP** - Funt szterling
- **CHF** - Frank szwajcarski

### ⚙️ Scheduler
```java
@Scheduled(cron = "0 0 12 * * MON-FRI")
public void updateExchangeRates() {
    // Automatyczna aktualizacja kursów
}
```

---

## 📋 Slajd 10: Bezpieczeństwo

### 🔐 Implementowane Mechanizmy

#### 1. Hashowanie Haseł
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode(rawPassword);
```

#### 2. JWT Authentication
```
POST /api/users/login
→ Returns: { "token": "eyJhbGciOiJIUzI1..." }

GET /api/wallets/me
Header: Authorization: Bearer <token>
```

**Token zawiera:**
- User ID
- Email
- Role
- Expiration (24h)

#### 3. Spring Security Configuration
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .csrf().disable()
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, ...);
}
```

#### 4. Validation
- Email format validation
- Password min 6 characters
- Amount > 0 validation
- Balance checks before exchange

---

## 📋 Slajd 11: Demonstracja - User Flow

### 🎬 Scenariusz Demo (5 minut)

#### 1️⃣ Rejestracja i Logowanie (1 min)
```
1. Otwórz aplikację
2. Kliknij "Zarejestruj się"
3. Wypełnij formularz:
   - Imię: Jan Kowalski
   - Email: jan@example.com
   - Hasło: test123
4. Zaloguj się
```

#### 2️⃣ Doładowanie Konta (1 min)
```
1. Ekran Portfel → "Doładuj (PayPal)"
2. Wprowadź kwotę: 1000 PLN
3. Potwierdź
4. Zobacz zaktualizowane saldo
```

#### 3️⃣ Wymiana Walut (2 min)
```
1. Kliknij "Wymień Walutę"
2. Z waluty: PLN
3. Na walutę: USD
4. Kwota: 400 PLN
5. Zobacz podgląd: "Otrzymasz ~100 USD"
6. Potwierdź wymianę
7. Sprawdź portfel - nowe saldo USD
```

#### 4️⃣ Historia Transakcji (1 min)
```
1. Kliknij "Historia Transakcji"
2. Zobacz wszystkie operacje:
   - TOP_UP: +1000 PLN
   - EXCHANGE: 400 PLN → 100 USD
```

---

## 📋 Slajd 12: Testy - Backend

### 🧪 Pokrycie Testami Backend

#### Statystyki:
- **9 plików testowych**
- **15+ test cases**
- **✅ 100% testów przechodzi**

#### Testowane Komponenty:

```
✅ NbpClientTest
   - Test połączenia z API NBP
   - Parsing JSON response

✅ AuthServiceTest
   - Rejestracja użytkownika
   - Logowanie
   - Walidacja danych

✅ ExchangeRateServiceTest
   - Pobieranie kursów
   - Cache mechanism
   - Obsługa błędów

✅ Repositories Tests
   - UserRepository
   - WalletRepository
   - TransactionRepository
   - ExchangeRateRepository

✅ Controllers Tests
   - AuthController
   - ExchangeRateController
```

#### Przykład Testu:
```java
@Test
void shouldRegisterNewUser() {
    RegisterDto dto = new RegisterDto("test@email.com", "pass123");
    AuthResponseDto response = authService.register(dto);
    assertNotNull(response.getToken());
}
```

---

## 📋 Slajd 13: Testy - Mobile

### 🧪 Pokrycie Testami Mobile

#### Statystyki:
- **4 pliki testowe**
- **15 test cases**
- **✅ 100% testów przechodzi**

#### Testowane Komponenty:

```
✅ App.test.tsx
   - Renderowanie aplikacji

✅ AuthContext.test.tsx (4 testy)
   - Sprawdzanie tokenu przy starcie
   - Login success
   - Login failure
   - Logout

✅ LoginScreen.test.tsx (4 testy)
   - Renderowanie
   - Walidacja pustych pól
   - Poprawne logowanie
   - Błędne dane

✅ RegisterScreen.test.tsx (6 testów)
   - Renderowanie
   - Walidacja formularza
   - Sprawdzanie zgodności haseł
   - Rejestracja success
   - Rejestracja failure
```

#### Przykład Testu:
```typescript
it('calls login with correct credentials', async () => {
  const { getByPlaceholderText, getByText } = render(<LoginScreen />);
  
  fireEvent.changeText(getByPlaceholderText('Email'), 'test@example.com');
  fireEvent.changeText(getByPlaceholderText('Password'), 'pass123');
  fireEvent.press(getByText('ZALOGUJ SIĘ'));
  
  await waitFor(() => {
    expect(mockLogin).toHaveBeenCalledWith('test@example.com', 'pass123');
  });
});
```

---

## 📋 Slajd 14: Statystyki Projektu

### 📊 Metryki Kodu

#### Backend (Java/Spring Boot):
```
📁 Struktura:
   - 5 Controllers
   - 3 Services  
   - 4 Repositories
   - 10 Models/DTOs
   - 4 Migracje SQL

📝 Linie kodu: ~2,500 LOC
🧪 Testy: 9 plików, 15+ test cases
✅ Build: SUCCESS
```

#### Mobile (React Native/TypeScript):
```
📁 Struktura:
   - 6 Screens
   - 3 Services
   - 1 Context (AuthContext)
   - 1 Hook (useExchangeRates)
   - 10+ Types

📝 Linie kodu: ~1,800 LOC
🧪 Testy: 4 pliki, 15 test cases
✅ Build: SUCCESS
```

#### Baza Danych:
```
📊 Tabele: 4
📝 Migracje: 4 (Flyway)
🔑 Foreign Keys: 3
🔒 Constraints: 8
```

### ⏱️ Czas Realizacji
- **Tydzień 1-2:** Analiza i projektowanie
- **Tydzień 3:** Setup środowiska
- **Tydzień 4:** Backend - Authentication
- **Tydzień 5-6:** Backend - NBP Integration
- **Tydzień 7-10:** Mobile App Development
- **Tydzień 11-12:** Testy i bugfixes
- **Tydzień 13:** Dokumentacja i prezentacja

---

## 📋 Slajd 15: CI/CD i DevOps

### 🚀 GitHub Actions Workflows

#### 1. Backend Build & Test
```yaml
name: Backend Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - Checkout code
      - Setup Java 17
      - Run Gradle tests
      - Upload coverage
```

#### 2. Mobile Tests
```yaml
name: Mobile Tests  
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - Checkout code
      - Setup Node.js
      - Run npm test
```

#### 3. Code Quality
```yaml
name: Code Quality
on: [push]
jobs:
  lint:
    - ESLint (Mobile)
    - Checkstyle (Backend)
```

### 🐳 Docker Setup
```yaml
services:
  postgres:
    image: postgres:16
    ports: ["5432:5432"]
  
  backend:
    build: ./backend
    ports: ["8080:8080"]
    depends_on: [postgres]
```

---

## 📋 Slajd 16: Wyzwania i Rozwiązania

### 🎯 Napotkane Wyzwania

#### 1️⃣ Problem: Kursy NBP w weekendy
**Wyzwanie:** NBP nie publikuje kursów w weekendy  
**Rozwiązanie:** 
- Cache ostatniego kursu roboczego
- Scheduler działa tylko w dni robocze (MON-FRI)

#### 2️⃣ Problem: Atomowość transakcji wymiany
**Wyzwanie:** Zapewnienie ACID dla operacji na 2 portfelach  
**Rozwiązanie:**
```java
@Transactional
public void exchange(...) {
    // Atomowa operacja:
    // 1. Zmniejsz saldo źródłowe
    // 2. Zwiększ saldo docelowe
    // 3. Zapisz transakcję
}
```

#### 3️⃣ Problem: JWT expiration handling
**Wyzwanie:** Graceful logout przy wygaśnięciu tokenu  
**Rozwiązanie:**
- Axios interceptor sprawdza 401
- Automatyczne przekierowanie do logowania

#### 4️⃣ Problem: Testy mobilne z AsyncStorage
**Wyzwanie:** Mock native modules w Jest  
**Rozwiązanie:**
```javascript
jest.mock('@react-native-async-storage/async-storage', () =>
  require('@react-native-async-storage/async-storage/jest/async-storage-mock')
);
```

---

## 📋 Slajd 17: Zgodność z Wymaganiami

### ✅ Checklist Wymagań Funkcjonalnych

| ID | Wymaganie | Status | Realizacja |
|----|-----------|--------|------------|
| **F1** | Rejestracja użytkownika | ✅ | RegisterScreen + AuthController |
| **F2** | Logowanie (JWT) | ✅ | LoginScreen + Security Config |
| **F3** | Zasilenie konta | ✅ | TopUpScreen + WalletController |
| **F4** | Pobieranie kursów NBP | ✅ | ExchangeRateController + Scheduler |
| **F5** | Wymiana walut | ✅ | ExchangeScreen + ExchangeController |
| **F6** | Historia transakcji | ✅ | HistoryScreen + TransactionController |

### ✅ Checklist Wymagań Niefunkcjonalnych

| ID | Wymaganie | Status | Realizacja |
|----|-----------|--------|------------|
| **N1** | Wydajność (≤500ms) | ✅ | Optymalizacja zapytań SQL, Cache |
| **N2** | BCrypt hashowanie | ✅ | Spring Security BCrypt |
| **N3** | HTTPS (TLS 1.3) | ✅ | Konfiguracja gotowa (prod) |
| **N4** | ACID transactions | ✅ | @Transactional w serwisach |

**Wszystkie wymagania z dokumentacji zostały spełnione!** 🎉

---

## 📋 Slajd 18: Możliwe Rozszerzenia

### 🚀 Funkcje Planowane (v1.1)

#### 📊 Wykresy Historyczne
- Wizualizacja zmian kursów (tydzień/miesiąc/rok)
- Integracja z biblioteką wykresów (react-native-chart-kit)

#### 🔔 Powiadomienia Push
- Alerty gdy kurs osiągnie próg
- Firebase Cloud Messaging
- Ustawienia preferencji użytkownika

#### 🌍 Internacjonalizacja (i18n)
- Wsparcie dla wielu języków (PL, EN)
- react-i18next integration

#### 💳 Prawdziwa Bramka Płatnicza
- Integracja z Stripe/PayPal API
- Obsługa prawdziwych transakcji
- Weryfikacja KYC/AML

#### 🎨 Panel Administracyjny
- Dashboard dla administratora
- Zarządzanie użytkownikami
- Statystyki systemu
- Manualna aktualizacja kursów

#### 📱 iOS Support
- Build aplikacji na iOS
- TestFlight distribution

---

## 📋 Slajd 19: Wnioski

### 🎓 Zdobyta Wiedza

#### Techniczne:
✅ **Architektura trójwarstwowa** - praktyczna implementacja  
✅ **REST API** - projektowanie i konsumpcja  
✅ **JWT Authentication** - bezpieczeństwo w praktyce  
✅ **React Native** - cross-platform development  
✅ **Spring Boot** - enterprise-grade backend  
✅ **PostgreSQL** - zaawansowane ORM (Hibernate)  
✅ **Docker** - konteneryzacja aplikacji  
✅ **Git & CI/CD** - współpraca zespołowa  

#### Miękkie:
✅ Praca w zespole (2 osoby)  
✅ Planowanie projektu (diagramy UML/ERD)  
✅ Dokumentacja techniczna  
✅ Prezentacja rezultatów  

### 💡 Najważniejsze Lekcje

1. **Testowanie jest kluczowe** - Testy uratowały nas przed wieloma bugami
2. **API design matters** - Dobre API ułatwia integrację
3. **Security first** - Bezpieczeństwo od początku, nie na końcu
4. **User experience** - Proste UI > Skomplikowane funkcje

---

## 📋 Slajd 20: Podsumowanie

### ✨ Osiągnięcia Projektu

✅ **Kompletny system** - Od bazy danych po aplikację mobilną  
✅ **Wszystkie wymagania spełnione** - F1-F6, N1-N4  
✅ **100% testów przechodzi** - Backend + Mobile  
✅ **Produkcyjna jakość kodu** - Clean Code, SOLID, DRY  
✅ **Dokumentacja** - Techniczna + Użytkownika  
✅ **CI/CD pipeline** - Automatyczne testy przy każdym pushu  

### 📊 Statystyki Finalne

```
📝 Łącznie linii kodu: ~4,300 LOC
🧪 Łącznie testów: 30+ test cases
⏱️ Czas realizacji: 13 tygodni
👥 Wielkość zespołu: 2 osoby
🐛 Znane bugi: 0 krytycznych
⭐ Ocena własna: 5.0 / 5.0
```

### 🙏 Podziękowania

Dziękujemy za uwagę!

**Pytania?** 🤔

---

**Kontakt:**
- GitHub: [link do repo]
- Email: [kontakt]

---

## 📋 BACKUP: Pytania i Odpowiedzi

### Przewidywane Pytania

**Q: Dlaczego React Native zamiast natywnego Androida?**  
A: React Native pozwala na cross-platform development (Android + iOS w przyszłości), szybszy development, łatwiejsze prototypowanie.

**Q: Jak obsługujecie błędy sieci?**  
A: Każde API call ma try-catch, użytkownik widzi czytelne komunikaty, retry mechanism w React Query.

**Q: Czy system jest skalowalny?**  
A: Tak - PostgreSQL może obsłużyć miliony rekordów, Spring Boot wspiera load balancing, możliwość horizontal scaling z Docker Swarm/Kubernetes.

**Q: Dlaczego PostgreSQL a nie MySQL?**  
A: PostgreSQL ma lepsze wsparcie dla JSON, transakcji, constraints, jest bardziej zgodny ze standardem SQL.

**Q: Jak długo token JWT jest ważny?**  
A: 24 godziny. Można to skonfigurować w application.yml.

**Q: Czy można dodać więcej walut?**  
A: Tak - wystarczy dodać kod waluty do NbpClient, system automatycznie pobierze kursy.

**Q: Jak testowaliście aplikację mobilną?**  
A: Android Studio Emulator + fizyczne urządzenie + Jest unit tests.

**Q: Czy projekt jest open source?**  
A: Obecnie prywatny (projekt edukacyjny), możliwe udostępnienie po zakończeniu kursu.
