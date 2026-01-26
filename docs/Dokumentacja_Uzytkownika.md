# Dokumentacja Użytkownika
## System Mobilny Kantoru Wymiany Walut

**Wersja:** 1.0  
**Data:** 26 stycznia 2026  
**Autorzy:** Mikołaj Przybysz, Jakub Dyba

---

## Spis Treści

1. [Wprowadzenie](#wprowadzenie)
2. [Wymagania Systemowe](#wymagania-systemowe)
3. [Instalacja i Konfiguracja](#instalacja-i-konfiguracja)
4. [Instrukcja Użytkowania](#instrukcja-użytkowania)
5. [Często Zadawane Pytania (FAQ)](#często-zadawane-pytania-faq)
6. [Rozwiązywanie Problemów](#rozwiązywanie-problemów)
7. [Kontakt i Wsparcie](#kontakt-i-wsparcie)

---

## Wprowadzenie

**System Mobilny Kantoru Wymiany Walut** to aplikacja umożliwiająca:
- 💰 Wymianę walut w czasie rzeczywistym (PLN, USD, EUR, GBP, CHF)
- 📊 Przeglądanie aktualnych kursów z Narodowego Banku Polskiego (NBP)
- 💳 Zarządzanie wirtualnym portfelem walutowym
- 📜 Dostęp do historii wszystkich transakcji
- 🔒 Bezpieczne logowanie z szyfrowaniem JWT

Aplikacja działa na platformie **Android** i wykorzystuje oficjalne kursy NBP aktualizowane codziennie.

---

## Wymagania Systemowe

### Aplikacja Mobilna (Android)
- **System operacyjny:** Android 8.0 (API 26) lub nowszy
- **Pamięć RAM:** Minimum 2 GB
- **Wolne miejsce:** 100 MB
- **Połączenie internetowe:** Wymagane

### Backend (Serwer)
- **Java:** 17 lub nowszy
- **PostgreSQL:** 13 lub nowszy
- **Docker:** 20.10+ (opcjonalnie, dla łatwego uruchomienia)

---

## Instalacja i Konfiguracja

### Dla Użytkowników Końcowych

#### Metoda 1: Instalacja APK (Najprostsza)

1. **Pobierz plik APK** aplikacji z repozytorium projektu
2. Na telefonie Android włącz **"Nieznane źródła"**:
   - Ustawienia → Bezpieczeństwo → Nieznane źródła (zaznacz)
3. **Otwórz pobrany plik APK** i postępuj zgodnie z instrukcjami instalatora
4. Po instalacji uruchom aplikację **"Kantor Walutowy"**

#### Metoda 2: Build z Źródła (dla programistów)

```bash
# Sklonuj repozytorium
git clone <repository-url>
cd currency-exchange-system/mobile

# Zainstaluj zależności
npm install

# Uruchom aplikację na podłączonym telefonie/emulatorze
npm run android
```

### Konfiguracja Backendu (dla administratorów)

#### Uruchomienie z Docker Compose (Zalecane)

```bash
cd backend
docker-compose up -d
```

Backend będzie dostępny pod adresem: `http://localhost:8080`

#### Uruchomienie manualne

```bash
# 1. Uruchom PostgreSQL
# 2. Skonfiguruj bazę danych w application.yml
# 3. Uruchom aplikację Spring Boot
./gradlew bootRun
```

---

## Instrukcja Użytkowania

### 1. Rejestracja Konta

<img src="screenshots/register.png" width="300" alt="Ekran rejestracji" />

**Krok po kroku:**

1. Uruchom aplikację
2. Na ekranie logowania kliknij **"Zarejestruj się"**
3. Wypełnij formularz:
   - **Imię:** Twoje imię (np. Jan Kowalski)
   - **Email:** Adres email (np. jan@example.com)
   - **Hasło:** Min. 6 znaków
   - **Powtórz hasło:** To samo hasło
4. Kliknij **"ZAREJESTRUJ SIĘ"**
5. Po pomyślnej rejestracji zostaniesz przekierowany do ekranu logowania

**Ważne:**
- Email musi być unikalny w systemie
- Hasło musi mieć minimum 6 znaków
- Wszystkie pola są wymagane

---

### 2. Logowanie

<img src="screenshots/login.png" width="300" alt="Ekran logowania" />

**Krok po kroku:**

1. Wprowadź swój **email** i **hasło**
2. Kliknij **"ZALOGUJ SIĘ"**
3. Po zalogowaniu zobaczysz ekran główny (Portfel)

**Uwaga:** System używa bezpiecznego tokenu JWT, który wygasa po określonym czasie. Jeśli zostaniesz wylogowany, po prostu zaloguj się ponownie.

---

### 3. Przeglądanie Portfela

<img src="screenshots/wallet.png" width="300" alt="Ekran portfela" />

Po zalogowaniu zobaczysz swój wirtualny portfel z dostępnymi środkami w różnych walutach:

```
┌──────────────────┐
│ PLN    1000.00   │  ← Portfel w złotych polskich
├──────────────────┤
│ USD      50.25   │  ← Portfel w dolarach amerykańskich
├──────────────────┤
│ EUR      30.00   │  ← Portfel w euro
└──────────────────┘
```

**Funkcje dostępne na ekranie:**
- **Wyloguj** (prawy górny róg) - wylogowanie z systemu
- **Wymień Walutę** - przejście do ekranu wymiany
- **Doładuj (PayPal)** - symulacja doładowania konta
- **Historia Transakcji** - przeglądanie historii operacji

**Odświeżanie salda:**
- Przesuń listę w dół (pull-to-refresh) aby odświeżyć stan portfela

---

### 4. Doładowanie Konta (Top-Up)

<img src="screenshots/topup.png" width="300" alt="Ekran doładowania" />

System pozwala doładować konto w PLN poprzez symulowaną płatność PayPal.

**Krok po kroku:**

1. Na ekranie Portfel kliknij **"Doładuj (PayPal)"**
2. Wprowadź kwotę w PLN (np. 500)
3. Kliknij **"DOŁADUJ KONTO"**
4. Poczekaj na potwierdzenie
5. Po sukcesie wrócisz do ekranu portfela z zaktualizowanym saldem

**Ważne:**
- Kwota musi być większa niż 0
- System dodaje środki natychmiast
- To jest symulacja - brak prawdziwej płatności

**Przykład:**
```
Stan przed: PLN 1000.00
Doładowanie: 500 PLN
Stan po: PLN 1500.00
```

---

### 5. Wymiana Walut

<img src="screenshots/exchange.png" width="300" alt="Ekran wymiany" />

Główna funkcja aplikacji - wymiana jednej waluty na inną według aktualnych kursów NBP.

**Krok po kroku:**

1. Na ekranie Portfel kliknij **"Wymień Walutę"**
2. Wybierz **walutę źródłową** (z jakiej wymieniasz, np. PLN)
3. Wybierz **walutę docelową** (na jaką wymieniasz, np. USD)
4. Wprowadź **kwotę** do wymiany
5. Zobacz **podgląd** wymiany:
   ```
   Wymiana: 400.00 PLN
   Kurs: 1 USD = 4.00 PLN
   Otrzymasz: 100.00 USD
   ```
6. Kliknij **"WYMIEŃ"**
7. Poczekaj na potwierdzenie transakcji

**Jak działa kurs?**
- Kursy są pobierane z API NBP (Narodowy Bank Polski)
- Aktualizacja następuje codziennie o godzinie 12:00
- System używa kursu średniego (mid) dla transakcji

**Przykład wymiany:**
```
Przed wymianą:
  PLN: 1000.00
  USD: 0.00

Wymiana: 400 PLN → USD
Kurs: 1 USD = 4.00 PLN

Po wymianie:
  PLN: 600.00
  USD: 100.00
```

**Walidacja:**
- ❌ Nie możesz wymienić więcej niż posiadasz
- ❌ Nie możesz wymienić tej samej waluty (PLN → PLN)
- ✅ Kwota musi być większa niż 0

---

### 6. Historia Transakcji

<img src="screenshots/history.png" width="300" alt="Ekran historii" />

Przeglądaj wszystkie swoje transakcje w jednym miejscu.

**Dostępne informacje:**

```
┌─────────────────────────────────┐
│ EXCHANGE                        │
│ 400.00 PLN → 100.00 USD         │
│ Kurs: 4.00                      │
│ 26/01/2026 14:30               │
│ Status: COMPLETED              │
└─────────────────────────────────┘
```

**Typy transakcji:**
- **TOP_UP** - Doładowanie konta
- **EXCHANGE** - Wymiana walut

**Statusy:**
- ✅ **COMPLETED** - Transakcja zakończona sukcesem
- ⏳ **PENDING** - W trakcie przetwarzania
- ❌ **FAILED** - Transakcja nieudana

**Filtrowanie:**
- Historia jest sortowana od najnowszych
- Możesz przewijać w dół aby zobaczyć starsze transakcje

---

## Często Zadawane Pytania (FAQ)

### 1. Czy moje dane są bezpieczne?

**Tak!** System wykorzystuje:
- 🔒 Szyfrowanie hasła algorytmem **BCrypt**
- 🔐 Tokeny **JWT** do autoryzacji
- 🌐 Komunikację **HTTPS** (w produkcji)
- 💾 Baza danych **PostgreSQL** z zasadą ACID

### 2. Skąd pochodzą kursy walut?

Kursy są pobierane z oficjalnego API **Narodowego Banku Polskiego (NBP)**:
- URL: http://api.nbp.pl/
- Aktualizacja: Codziennie o 12:00 (dni robocze)
- Typ kursu: Średni (mid)

### 3. Czy mogę wymienić walutę w weekendy?

**Tak**, ale kurs będzie z ostatniego dnia roboczego. NBP publikuje kursy tylko w dni robocze.

### 4. Co jeśli nie mam wystarczających środków?

System nie pozwoli na wymianę. Zobaczysz komunikat:
```
❌ Niewystarczające środki w portfelu PLN
```

Rozwiązanie: Doładuj konto funkcją **"Doładuj (PayPal)"**

### 5. Czy mogę anulować transakcję?

**Nie**. Transakcje są natychmiastowe i nieodwracalne. Zawsze sprawdzaj kwoty przed potwierdzeniem.

### 6. Ile wynosi minimalna kwota wymiany?

**Brak minimalnej kwoty**, ale musi być większa niż 0. Możesz wymienić nawet 0.01 PLN.

### 7. Czy system pobiera prowizję?

**Nie**. System używa kursu NBP bez dodatkowych opłat. To projekt edukacyjny.

### 8. Co zrobić jeśli zapomniałem hasła?

Obecnie system nie ma funkcji resetowania hasła. Skontaktuj się z administratorem systemu.

### 9. Dlaczego widzę "Brak środków"?

To oznacza, że Twój portfel jest pusty. Aby rozpocząć:
1. Doładuj konto PLN funkcją **"Doładuj (PayPal)"**
2. Możesz wymienić PLN na inne waluty

### 10. Czy mogę używać aplikacji offline?

**Nie**. Aplikacja wymaga połączenia z internetem aby:
- Pobierać aktualne kursy walut
- Komunikować się z serwerem
- Aktualizować stan portfela

---

## Rozwiązywanie Problemów

### Problem: Nie mogę się zalogować

**Możliwe przyczyny i rozwiązania:**

1. **Błędne hasło lub email**
   - Sprawdź CAPS LOCK
   - Upewnij się, że email jest poprawny

2. **Brak połączenia z internetem**
   - Sprawdź Wi-Fi lub dane komórkowe
   - Sprawdź czy backend działa (dla administratorów)

3. **Konto nie istnieje**
   - Zarejestruj się najpierw
   - Email musi być dokładnie taki sam jak przy rejestracji

### Problem: Aplikacja się zawiesza

**Rozwiązania:**
1. Zamknij i uruchom aplikację ponownie
2. Wyczyść cache aplikacji:
   - Ustawienia → Aplikacje → Kantor Walutowy → Wyczyść cache
3. Odinstaluj i zainstaluj ponownie

### Problem: Kursy walut się nie aktualizują

**Możliwe przyczyny:**
1. **Weekend lub święta** - NBP nie publikuje kursów
2. **Problem z API NBP** - Tymczasowa awaria serwisu
3. **Brak internetu** - Sprawdź połączenie

**Co zrobić:**
- Poczekaj kilka minut i spróbuj ponownie
- Odśwież ekran (pull-to-refresh)

### Problem: "Niewystarczające środki" mimo że mam pieniądze

**Sprawdź:**
1. Czy wybierasz poprawną walutę źródłową?
2. Czy kwota nie przekracza dostępnego salda?
3. Czy portfel został odświeżony? (przeciągnij w dół)

### Problem: Backend nie działa (dla administratorów)

**Sprawdź:**

```bash
# 1. Czy backend jest uruchomiony?
curl http://localhost:8080/api/rates/current

# 2. Czy PostgreSQL działa?
docker ps | grep postgres

# 3. Sprawdź logi
docker-compose logs -f backend

# 4. Restart serwisów
docker-compose restart
```

### Problem: Błąd "Network Error" w aplikacji

**Kroki naprawcze:**

1. **Sprawdź konfigurację backendu:**
   ```typescript
   // mobile/src/constants/config.ts
   export const APP_CONFIG = {
     API_BASE_URL: 'http://10.0.2.2:8080/api', // Android Emulator
     // lub
     API_BASE_URL: 'http://<YOUR_IP>:8080/api', // Fizyczne urządzenie
   };
   ```

2. **Dla emulatora Android:**
   - Użyj `10.0.2.2` zamiast `localhost`

3. **Dla fizycznego telefonu:**
   - Telefon i komputer muszą być w tej samej sieci Wi-Fi
   - Użyj lokalnego IP komputera (np. `192.168.1.100`)

---

## Kontakt i Wsparcie

### Zgłaszanie Błędów

Jeśli znalazłeś błąd lub masz sugestię:

1. **GitHub Issues:** <repository-url>/issues
2. **Email:** [Twój email kontaktowy]

**Przy zgłaszaniu podaj:**
- Wersję aplikacji
- Wersję Androida
- Kroki prowadzące do błędu
- Screenshot (jeśli możliwe)

### Autorzy Projektu

- **Mikołaj Przybysz** - Backend & Integracja
- **Jakub Dyba** - Mobile & Frontend

### Licencja

Projekt edukacyjny - Akademia Ekonomiczno-Humanistyczna w Warszawie  
Przedmiot: Zagadnienia sieciowe w systemach mobilnych  
Rok akademicki: 2024/2025

---

## Dodatek: Skróty Klawiszowe (dla programistów)

### React Native Dev Menu

- **Android:** Wstrząśnij telefon lub Ctrl+M (emulator)
- **Reload:** Naciśnij `R` dwukrotnie

### Użyteczne komendy

```bash
# Uruchom testy
npm test

# Sprawdź linting
npm run lint

# Build release APK
cd android
./gradlew assembleRelease

# Wyczyść cache
npm start -- --reset-cache
```

---

## Changelog

### Wersja 1.0 (26.01.2026)
- ✅ Rejestracja i logowanie użytkowników
- ✅ Zarządzanie portfelem walutowym
- ✅ Wymiana walut (PLN, USD, EUR, GBP, CHF)
- ✅ Integracja z API NBP
- ✅ Historia transakcji
- ✅ Doładowanie konta (symulacja PayPal)
- ✅ Automatyczna aktualizacja kursów
- ✅ Testy jednostkowe i integracyjne

### Planowane funkcje (v1.1)
- 📊 Wykresy historyczne kursów walut
- 🔔 Powiadomienia push o zmianach kursów
- 🌍 Wsparcie dla wielu języków (i18n)
- 💳 Integracja z prawdziwą bramką płatniczą

---

**Dziękujemy za korzystanie z systemu! 🎉**
