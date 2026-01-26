# 🚀 Instrukcja Uruchomienia - Nowe Funkcje

## Jak Zobaczyć Wykresy Historyczne na Emulatorze

### Krok 1: Upewnij się że Backend działa

```bash
# Terminal 1 - Uruchom backend
cd backend
docker-compose up -d

# Sprawdź czy działa
curl http://localhost:8080/api/rates/current
```

Jeśli widzisz JSON z kursami walut - backend działa! ✅

---

### Krok 2: Zainstaluj nowe zależności mobile

```bash
cd mobile

# Zainstaluj nowe paczki (wykresy)
npm install

# Dla iOS (jeśli używasz Mac)
cd ios && pod install && cd ..
```

---

### Krok 3: Uruchom Metro Bundler

```bash
# W katalogu mobile
npm start

# Lub z czyszczeniem cache (jeśli były problemy)
npm start -- --reset-cache
```

Zostaw to okno otwarte! Metro musi działać w tle.

---

### Krok 4: Uruchom Emulator Android

#### Opcja A: Android Studio
1. Otwórz Android Studio
2. Tools → Device Manager
3. Uruchom emulator (np. Pixel 5 API 33)

#### Opcja B: Terminal
```bash
# Lista dostępnych emulatorów
emulator -list-avds

# Uruchom wybrany emulator
emulator -avd Pixel_5_API_33 &
```

---

### Krok 5: Zainstaluj Aplikację

```bash
# W nowym terminalu (Metro musi być uruchomione)
cd mobile
npm run android
```

Aplikacja się zainstaluje i uruchomi! ⏳

---

### Krok 6: Zobacz Nowe Funkcje! 🎉

#### Nawigacja do Wykresów:

1. **Zaloguj się** (lub zarejestruj jeśli nowe konto)
   - Email: `test@example.com`
   - Hasło: `test123`

2. **Ekran Portfela** - zobaczysz nowy przycisk:
   ```
   [Wymień Walutę]
   [Doładuj (PayPal)]
   [Historia Transakcji]
   [📊 Wykresy Kursów]  ← NOWY!
   ```

3. **Kliknij "📊 Wykresy Kursów"**

4. **Eksploruj funkcje:**
   - Wybierz walutę: USD, EUR, GBP, CHF
   - Wybierz okres: 7 dni, 30 dni, 90 dni
   - Zobacz wykres liniowy zmian kursu
   - Sprawdź statystyki: min, max, średnia, zmiana %

---

## 📱 Demo Flow - Pełna Ścieżka

### Scenariusz 1: Nowy Użytkownik

```
1. [Ekran Login] → Kliknij "Zarejestruj się"
2. [Ekran Rejestracji]:
   - Imię: Jan Kowalski
   - Email: jan@test.com
   - Hasło: test123
   - Potwierdź hasło: test123
   - Kliknij "ZAREJESTRUJ SIĘ"
   
3. [Alert] "Sukces! Możesz się teraz zalogować"
   - Kliknij OK → Powrót do logowania
   
4. [Ekran Login]:
   - Email: jan@test.com
   - Hasło: test123
   - Kliknij "ZALOGUJ SIĘ"
   
5. [Ekran Portfela] - Zobaczysz puste portfele
   - PLN: 0.00
   - USD: 0.00
   - EUR: 0.00
   - GBP: 0.00
   - CHF: 0.00
   
6. Kliknij "Doładuj (PayPal)"
   - Wpisz kwotę: 1000
   - Kliknij "DOŁADUJ KONTO"
   - PLN teraz: 1000.00 ✅
   
7. Kliknij "Wymień Walutę"
   - Z waluty: PLN
   - Na walutę: USD
   - Kwota: 400
   - Zobacz podgląd: "Otrzymasz ~100 USD"
   - Kliknij "WYMIEŃ"
   
8. [Ekran Portfela]:
   - PLN: 600.00
   - USD: 100.00 ✅
   
9. Kliknij "Historia Transakcji"
   - Zobacz TOP_UP: +1000 PLN
   - Zobacz EXCHANGE: 400 PLN → 100 USD
   
10. Kliknij "📊 Wykresy Kursów" ← NOWOŚĆ!
```

### Scenariusz 2: Eksploracja Wykresów

```
[Ekran Wykresów]

1. Wybierz walutę USD (domyślnie wybrana)
2. Wybierz okres 30 dni (domyślnie)
3. Zobacz wykres - linia pokazująca zmiany kursu USD/PLN

4. Sprawdź statystyki:
   ┌─────────────┬─────────────┐
   │ Aktualny    │ Zmiana      │
   │ 4.0123      │ +2.15%      │
   ├─────────────┼─────────────┤
   │ Minimum     │ Maximum     │
   │ 3.9500      │ 4.0500      │
   ├─────────────┼─────────────┤
   │ Średnia     │ Próbki      │
   │ 4.0012      │ 30          │
   └─────────────┴─────────────┘

5. Zmień walutę na EUR - wykres się aktualizuje
6. Zmień okres na 7 dni - wykres pokazuje krótszy zakres
7. Zmień okres na 90 dni - wykres pokazuje dłuższy trend

8. Przewiń w dół - zobacz informacje:
   ℹ️ Kursy pobierane z API NBP
   📊 Dane aktualizowane codziennie w dni robocze
```

---

## 🔧 Troubleshooting

### Problem: "No bundle URL present"

**Rozwiązanie:**
```bash
# Upewnij się że Metro działa
cd mobile
npm start -- --reset-cache

# W innym terminalu
npm run android
```

### Problem: "Could not connect to development server"

**Rozwiązanie:**
```bash
# Sprawdź IP komputera
ipconfig getifaddr en0  # Mac
# lub
ipconfig  # Windows

# Ustaw w mobile/src/constants/config.ts
API_BASE_URL: 'http://192.168.1.XXX:8080/api'  # Twoje IP

# Restart Metro
npm start -- --reset-cache
```

### Problem: Backend nie odpowiada

**Rozwiązanie:**
```bash
# Sprawdź logi backendu
cd backend
docker-compose logs -f

# Restart backendu
docker-compose restart

# Sprawdź czy PostgreSQL działa
docker-compose ps
```

### Problem: Wykresy nie ładują się

**Możliwe przyczyny:**

1. **Brak danych historycznych w bazie**
   ```bash
   # Backend automatycznie pobiera przy starcie
   # Poczekaj 10 sekund po uruchomieniu
   ```

2. **Weekend lub święta**
   ```
   NBP nie publikuje kursów w weekendy
   System pokaże ostatnie dostępne dane
   ```

3. **Problem z API NBP**
   ```bash
   # Sprawdź czy NBP API działa
   curl "http://api.nbp.pl/api/exchangerates/rates/a/usd/last/30"
   ```

---

## 📱 Hot Reload - Testowanie Zmian

Po uruchomieniu aplikacji możesz edytować kod i od razu widzieć zmiany:

```bash
# Zmień coś w kodzie, np. kolor przycisku
# mobile/src/screens/ExchangeRateChartsScreen.tsx

# W emulatorze naciśnij:
R + R  (2x R) - Reload
# lub
Cmd/Ctrl + M → Reload
```

---

## 🎨 Dev Menu - Przydatne Opcje

**Android Emulator:**
```
Cmd/Ctrl + M → Otwórz Dev Menu

Opcje:
- Reload - Odśwież aplikację
- Debug - Otwórz Chrome DevTools
- Enable Fast Refresh - Auto-reload przy zmianach
- Show Perf Monitor - Monitor wydajności
```

**Fizyczne Urządzenie:**
```
Potrząśnij telefonem → Dev Menu
```

---

## 📊 Testowanie Wszystkich Funkcji

### Checklist do przetestowania:

```
✅ Rejestracja
  □ Walidacja pustych pól
  □ Walidacja emaila
  □ Walidacja zgodności haseł
  □ Sukces rejestracji

✅ Logowanie
  □ Błędne dane → Komunikat o błędzie
  □ Poprawne dane → Przejście do portfela

✅ Portfel
  □ Wyświetlanie sald
  □ Pull-to-refresh
  □ Przycisk wylogowania

✅ Doładowanie
  □ Walidacja kwoty (>0)
  □ Sukces → Zaktualizowane saldo PLN

✅ Wymiana
  □ Wybór walut
  □ Podgląd kursu i kwoty
  □ Walidacja salda
  □ Sukces → Zaktualizowane salda

✅ Historia
  □ Lista transakcji
  □ Szczegóły transakcji (typ, kwota, kurs, data)

✅ Wykresy (NOWE!) 📊
  □ Wybór waluty (USD/EUR/GBP/CHF)
  □ Wybór okresu (7d/30d/90d)
  □ Wyświetlanie wykresu liniowego
  □ Statystyki (min/max/średnia/zmiana)
  □ Loading state
  □ Obsługa błędów
```

---

## 🎥 Nagrywanie Demo

Jeśli chcesz nagrać wideo demonstracyjne:

```bash
# Android - nagrywanie ekranu przez ADB
adb shell screenrecord /sdcard/demo.mp4

# Zatrzymaj nagrywanie: Ctrl+C

# Pobierz video
adb pull /sdcard/demo.mp4
```

---

## ✅ Quick Start - TL;DR

```bash
# 1. Backend (terminal 1)
cd backend && docker-compose up -d

# 2. Metro (terminal 2)
cd mobile && npm install && npm start

# 3. Android (terminal 3)
cd mobile && npm run android

# 4. W aplikacji:
Login → Portfel → "📊 Wykresy Kursów"
```

---

## 🎉 Gotowe!

Teraz możesz:
- ✅ Testować wszystkie funkcje
- ✅ Zobaczyć nowe wykresy historyczne
- ✅ Przygotować demo na prezentację
- ✅ Robić screenshoty do dokumentacji

**Powodzenia z prezentacją projektu!** 🚀
