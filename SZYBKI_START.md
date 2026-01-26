# 🚀 Szybki Start - Nowa Funkcja Wykresy Kursów

## Kompletna Instrukcja od Zera

### Krok 1: Sprawdź Backend ✅

```bash
# Terminal 1 - Backend
cd backend
docker-compose ps

# Jeśli nie działa:
docker-compose up -d

# Sprawdź:
curl http://localhost:8080/api/rates/current
```

Powinieneś zobaczyć JSON z kursami walut.

---

### Krok 2: Uruchom Emulator Android 📱

#### Opcja A: Android Studio
1. Otwórz Android Studio
2. Tools → Device Manager
3. Uruchom emulator (np. Pixel 5 API 33)

#### Opcja B: Terminal
```bash
# Lista emulatorów:
emulator -list-avds

# Uruchom wybrany:
emulator -avd Pixel_5_API_33 &

# Sprawdź czy działa:
adb devices
# Powinno pokazać: emulator-5554 device
```

---

### Krok 3: Metro Bundler 📦

```bash
# Terminal 2 - Metro Bundler
cd mobile
npm start

# ZOSTAW TO OKNO OTWARTE!
# Poczekaj aż zobaczysz:
# ✓ Metro Bundler ready
```

---

### Krok 4: Zainstaluj Aplikację 📲

```bash
# Terminal 3 - Nowy terminal!
cd mobile
npm run android

# Poczekaj 30-60 sekund na:
# - Build APK
# - Instalację
# - Automatyczne uruchomienie

# Sukces gdy zobaczysz:
# BUILD SUCCESSFUL
# Installing APK...
# Starting: Intent...
```

---

### Krok 5: Testuj Nową Funkcję! 🎉

#### W Aplikacji na Emulatorze:

1. **Zaloguj się:**
   ```
   Email: test@example.com
   Hasło: test123
   ```
   (Lub zarejestruj nowe konto)

2. **Ekran Portfel:**
   - Zobaczysz swoje salda
   - Przewiń w DÓŁ

3. **Znajdź nowy przycisk:**
   ```
   [Wymień Walutę]
   [Doładuj (PayPal)]
   [Historia Transakcji]
   [📊 Wykresy Kursów]  ← NOWY!
   ```

4. **Kliknij [📊 Wykresy Kursów]**

5. **Eksploruj funkcje:**
   - Wybierz walutę: USD, EUR, GBP, CHF
   - Wybierz okres: 7 dni, 30 dni, 90 dni
   - Zobacz dane historyczne
   - Sprawdź statystyki

---

## 📊 Co Zobaczysz na Ekranie Wykresów

```
╔═══════════════════════════════════════╗
║  Wykresy Kursów Walut                 ║
║  Analiza historyczna z NBP            ║
╠═══════════════════════════════════════╣
║                                       ║
║  Wybierz walutę:                      ║
║  [USD] [EUR] [GBP] [CHF]              ║
║                                       ║
║  Okres czasu:                         ║
║  [7 dni] [30 dni] [90 dni]            ║
║                                       ║
╟───────────────────────────────────────╢
║  📊 Dane historyczne dla Dolar (USD)  ║
║  📅 Okres: 30 dni                     ║
║                                       ║
║  📋 Ostatnie kursy:                   ║
║  2026-01-26    4.0123 PLN            ║
║  2026-01-25    4.0089 PLN            ║
║  2026-01-24    3.9975 PLN            ║
║  2026-01-23    4.0156 PLN            ║
║  2026-01-22    4.0234 PLN            ║
║  ... i 25 więcej punktów danych      ║
║                                       ║
╟───────────────────────────────────────╢
║  📊 STATYSTYKI                        ║
║                                       ║
║  ┌─────────────┬─────────────┐       ║
║  │ Aktualny    │ Zmiana      │       ║
║  │ 4.0123      │ +2.15%      │       ║
║  ├─────────────┼─────────────┤       ║
║  │ Minimum     │ Maximum     │       ║
║  │ 3.9500      │ 4.0500      │       ║
║  ├─────────────┼─────────────┤       ║
║  │ Średnia     │ Próbki      │       ║
║  │ 4.0012      │ 30          │       ║
║  └─────────────┴─────────────┘       ║
║                                       ║
║  ℹ️ Kursy z API NBP                   ║
║  📊 Aktualizowane codziennie          ║
╚═══════════════════════════════════════╝
```

---

## 🔧 Troubleshooting

### Problem: Metro już działa (port 8081 zajęty)

```bash
# Zatrzymaj stare procesy:
pkill -f metro
pkill -f "node.*8081"

# Uruchom ponownie:
cd mobile
npm start
```

### Problem: Build fails

```bash
# Wyczyść wszystko:
cd mobile/android
./gradlew clean

# Wyczyść cache Metro:
cd ..
npm start -- --reset-cache
```

### Problem: Aplikacja nie instaluje się

```bash
# Wyczyść dane aplikacji:
adb shell pm clear com.mobile

# Odinstaluj:
adb uninstall com.mobile

# Zainstaluj od nowa:
cd mobile
npm run android
```

### Problem: Emulator nie odpowiada

```bash
# Restart emulatora:
adb reboot

# Lub zamknij i uruchom ponownie z Android Studio
```

---

## 📝 Quick Commands Cheatsheet

```bash
# BACKEND
cd backend && docker-compose up -d              # Start
docker-compose ps                               # Status
curl http://localhost:8080/api/rates/current   # Test

# EMULATOR
emulator -list-avds                            # Lista
emulator -avd Pixel_5_API_33 &                 # Start
adb devices                                     # Sprawdź

# METRO
cd mobile && npm start                         # Start Metro
pkill -f metro                                 # Stop Metro

# APLIKACJA
cd mobile && npm run android                   # Install & Run
adb shell am force-stop com.mobile             # Stop app
adb shell pm clear com.mobile                  # Clear data
adb uninstall com.mobile                       # Uninstall

# LOGI
npx react-native log-android                   # React Native logs
adb logcat | grep com.mobile                   # Android logs
```

---

## 🎯 Pełny Demo Scenariusz

### Scenariusz: Nowy Użytkownik Testuje Wykresy

```
1. REJESTRACJA
   → Email: demo@test.com
   → Hasło: demo123
   → ZAREJESTRUJ SIĘ

2. LOGOWANIE
   → Email: demo@test.com
   → Hasło: demo123
   → ZALOGUJ SIĘ

3. DOŁADOWANIE (opcjonalne)
   → Kliknij "Doładuj (PayPal)"
   → Kwota: 1000 PLN
   → DOŁADUJ

4. WYKRESY (NOWA FUNKCJA!)
   → Przewiń w dół na ekranie Portfel
   → Kliknij "📊 Wykresy Kursów"
   
5. TESTUJ WYKRESY
   → Wybierz USD
   → Wybierz 30 dni
   → Zobacz dane i statystyki
   
   → Zmień na EUR
   → Zobacz jak dane się aktualizują
   
   → Zmień na 7 dni
   → Zobacz krótszy okres
   
   → Zmień na 90 dni
   → Zobacz długi trend

6. SPRAWDŹ STATYSTYKI
   → Min/Max - zakres wahań
   → Średnia - średni kurs
   → Zmiana % - trend wzrostowy/spadkowy
   → Liczba próbek - ile punktów danych
```

---

## ✅ Checklist Funkcjonalności

Po uruchomieniu sprawdź:

```
□ Backend działa (curl zwraca kursy)
□ Emulator uruchomiony (adb devices)
□ Metro bundler aktywny
□ Aplikacja zainstalowana
□ Logowanie działa
□ Ekran Portfel widoczny
□ Przycisk "📊 Wykresy Kursów" widoczny
□ Kliknięcie otwiera ekran wykresów
□ Wybór waluty działa
□ Wybór okresu działa
□ Dane się ładują
□ Statystyki są wyświetlane
□ Wszystko bez błędów!
```

---

## 🎉 Gotowe!

Teraz masz:
- ✅ Działającą aplikację
- ✅ Nową funkcję wykresów historycznych
- ✅ Wszystkie funkcje podstawowe
- ✅ Pełną dokumentację

**Powodzenia z testowaniem i prezentacją!** 🚀

---

## 💡 Pro Tips

1. **Pull-to-refresh** - Na ekranie portfela przeciągnij w dół aby odświeżyć
2. **Fast Refresh** - Metro automatycznie odświeża po zmianach w kodzie
3. **Dev Menu** - Cmd/Ctrl+M na emulatorze → Reload/Debug
4. **Chrome DevTools** - Cmd/Ctrl+M → Debug → Pełny debugger
5. **Logi na żywo** - `npx react-native log-android` w osobnym terminalu

---

Ostatnia aktualizacja: 26.01.2026
