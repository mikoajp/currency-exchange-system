# 🚀 Jak Uruchomić Projekt - Kompletna Instrukcja

## KROK 1: Uruchom Backend

### Terminal 1 - Backend
```bash
cd backend
docker-compose up -d
```

**Sprawdź czy działa:**
```bash
curl http://localhost:8080/api/rates/current
```

Powinno zwrócić JSON z kursami walut.

---

## KROK 2: Uruchom Emulator Android

### Opcja A: Przez Android Studio (ZALECANE)

1. **Otwórz Android Studio**
2. Kliknij **Tools** → **Device Manager** (lub ikona telefonu na pasku)
3. Wybierz emulator z listy (np. Pixel 5 API 33)
4. Kliknij **▶ (Play)** aby uruchomić

### Opcja B: Przez Terminal

```bash
# 1. Zobacz listę dostępnych emulatorów
emulator -list-avds

# Przykład output:
# Pixel_5_API_33
# Pixel_6_API_34

# 2. Uruchom wybrany emulator
emulator -avd Pixel_5_API_33 &

# Symbol & na końcu uruchamia w tle
```

**Sprawdź czy działa:**
```bash
adb devices

# Output powinien pokazać:
# List of devices attached
# emulator-5554    device
```

---

## KROK 3: Uruchom Metro Bundler

### Terminal 2 - Metro Bundler
```bash
cd mobile
npm start
```

**Co zobaczysz:**
```
                 ######                ######
               ###     ####        ####     ###
              ##          ###    ###          ##
              ##             ####             ##
              ##             ####             ##
              ##           ##    ##           ##
              ##         ###      ###         ##
               ##  ########################  ##
            ######    ###            ###    ######
          ###     ##    ##          ##    ##     ###
         ##       ##    ##          ##    ##       ##
        ##            ###      ######      ###            ##
         ##        ##    ##  ##      ##  ##    ##        ##
          ##      ##      ##          ##      ##      ##
           ##    ##        ##        ##        ##    ##

 ✓ Metro Bundler ready
```

**ZOSTAW TO OKNO OTWARTE!** Metro musi działać cały czas.

---

## KROK 4: Zainstaluj Aplikację na Emulatorze

### Terminal 3 - Instalacja (NOWY terminal!)
```bash
cd mobile
npm run android
```

**Co się dzieje:**
1. Gradle buduje APK (~30-60 sekund)
2. Instalacja na emulatorze
3. Automatyczne uruchomienie aplikacji

**Sukces gdy zobaczysz:**
```
BUILD SUCCESSFUL in 45s
Installing APK 'app-debug.apk' on 'Pixel_5_API_33'
Installed on 1 device.
Starting: Intent...
```

---

## ✅ Aplikacja Powinna Się Uruchomić!

Na emulatorze zobaczysz ekran logowania.

---

## 🎯 Quick Start - Wszystko w Jednym

Jeśli masz już wszystko skonfigurowane:

```bash
# Terminal 1: Backend
cd backend && docker-compose up -d

# Terminal 2: Metro (zostaw otwarte)
cd mobile && npm start

# Terminal 3: Instalacja (po ~10 sek gdy Metro gotowy)
cd mobile && npm run android
```

---

## 🔧 Troubleshooting

### Problem: "emulator: command not found"

**Rozwiązanie:**
```bash
# Dodaj do ~/.zshrc lub ~/.bashrc:
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Przeładuj:
source ~/.zshrc
```

### Problem: "No emulators available"

**Rozwiązanie:**
1. Otwórz Android Studio
2. Tools → Device Manager
3. Kliknij **+** (Create Device)
4. Wybierz Pixel 5 → Next
5. Wybierz System Image (API 33) → Next → Finish

### Problem: "Metro port 8081 already in use"

**Rozwiązanie:**
```bash
# Zatrzymaj stary proces:
lsof -ti:8081 | xargs kill -9

# Uruchom ponownie:
cd mobile && npm start
```

### Problem: "BUILD FAILED"

**Rozwiązanie:**
```bash
# Wyczyść cache i rebuiluj:
cd mobile/android
./gradlew clean

cd ..
npm start -- --reset-cache

# W nowym terminalu:
npm run android
```

### Problem: Emulator bardzo wolny

**Rozwiązanie:**
1. Android Studio → Device Manager
2. Edytuj emulator (ikona ✏️)
3. Show Advanced Settings
4. Zwiększ RAM do 2048 MB
5. Włącz Hardware Acceleration

### Problem: "Could not connect to development server"

**Rozwiązanie:**
```bash
# Emulator nie widzi localhost
# Zmień w mobile/src/constants/config.ts:

# Z:
API_BASE_URL: 'http://localhost:8080/api'

# Na (dla emulatora Android):
API_BASE_URL: 'http://10.0.2.2:8080/api'
```

---

## 📱 Testowanie Aplikacji

Po uruchomieniu:

1. **Zaloguj się:**
   - Email: test@example.com
   - Hasło: test123
   
   (lub zarejestruj nowe konto)

2. **Testuj funkcje:**
   - ✅ Portfel (wyświetlanie sald)
   - ✅ Doładowanie konta
   - ✅ Historia transakcji
   - ⚠️ Wykresy kursów (może być bug)
   - ⚠️ Wymiana walut (może być bug)

---

## 🔄 Restart Wszystkiego

Jeśli coś nie działa, restart wszystkiego:

```bash
# 1. Zatrzymaj Metro (Ctrl+C w terminalu 2)

# 2. Zatrzymaj backend
cd backend && docker-compose down

# 3. Zatrzymaj aplikację
adb shell am force-stop com.mobile

# 4. Restart emulatora
adb reboot
# Lub zamknij i uruchom ponownie z Android Studio

# 5. Uruchom wszystko od nowa (Krok 1-4)
```

---

## 💡 Przydatne Komendy

```bash
# EMULATOR
adb devices                           # Lista urządzeń
adb shell                             # Shell w emulatorze
adb logcat                            # System logs
adb reboot                            # Restart emulatora

# APLIKACJA
adb shell am force-stop com.mobile    # Zatrzymaj app
adb shell pm clear com.mobile         # Wyczyść dane app
adb uninstall com.mobile              # Odinstaluj app
adb install app-debug.apk             # Zainstaluj APK

# METRO
npm start                             # Start Metro
npm start -- --reset-cache            # Start z czyszczeniem cache
pkill -f metro                        # Zatrzymaj Metro

# RELOAD W APLIKACJI
# Na emulatorze naciśnij:
R + R                                 # Szybki reload (2x R)
Cmd/Ctrl + M → Reload                 # Menu reload

# BACKEND
docker-compose ps                     # Status
docker-compose logs -f                # Logi
docker-compose restart                # Restart
```

---

## 🎨 Dev Menu (Przydatne podczas rozwoju)

### Jak otworzyć:
- **Android Emulator:** Cmd+M (Mac) lub Ctrl+M (Windows/Linux)
- **Fizyczne urządzenie:** Potrząśnij telefonem

### Opcje w Dev Menu:
- **Reload** - Przeładuj aplikację
- **Debug** - Otwórz Chrome DevTools
- **Enable Fast Refresh** - Auto-reload przy zmianach
- **Toggle Inspector** - Inspektor elementów UI
- **Show Perf Monitor** - Monitor wydajności

---

## 📊 Logi i Debugowanie

```bash
# React Native logs (najlepsze dla JS):
npx react-native log-android

# Wszystkie system logs:
adb logcat

# Tylko aplikacja:
adb logcat | grep com.mobile

# Tylko błędy:
adb logcat *:E
```

---

## ✅ Checklist - Czy Wszystko Działa?

```
□ Backend odpowiada (curl zwraca kursy)
□ Emulator uruchomiony (adb devices pokazuje device)
□ Metro Bundler działa (terminal 2 aktywny)
□ Aplikacja zainstalowana (npm run android SUCCESS)
□ Aplikacja się uruchamia na emulatorze
□ Mogę się zalogować
□ Ekran Portfel widoczny
□ Backend logs nie pokazują błędów
```

---

## 🆘 Szybka Pomoc

### Nie wiem który terminal co robi:
```
Terminal 1: Backend (docker-compose) - może być w tle
Terminal 2: Metro Bundler - MUSI być otwarty cały czas
Terminal 3: Komendy (npm run android, logi, itp.)
```

### Aplikacja nie łączy się z backend:
```bash
# Sprawdź:
1. Backend działa? → curl http://localhost:8080/api/rates/current
2. Config prawidłowy? → cat mobile/src/constants/config.ts
3. Dla emulatora użyj: 10.0.2.2 zamiast localhost
```

### Metro przestał działać:
```bash
pkill -f metro
cd mobile
npm start -- --reset-cache
```

---

Powodzenia! 🚀
