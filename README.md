# Currency Exchange System - Instrukcja Uruchomienia (Local Dev)

Projekt składa się z backendu (Spring Boot + PostgreSQL) oraz aplikacji mobilnej (React Native).

## 1. Wymagania wstępne
*   **Java JDK 17** (sprawdź: `java -version`)
*   **Node.js 18+** (sprawdź: `node -v`)
*   **Docker** (do bazy danych)
*   **Android Studio + SDK** (dla Androida)
*   **Xcode** (tylko dla iOS na macOS)

## 2. Backend (Serwer)

### Baza danych
Uruchom bazę danych PostgreSQL w kontenerze Docker:
```bash
cd backend
docker-compose up -d postgres
```
*Domyślne dane: user=`postgres`, pass=`postgres`, db=`currency_exchange_db`, port=`5432`.*

### Aplikacja
W tym samym terminalu uruchom backend:
```bash
./gradlew bootRun
```
Backend wystartuje pod adresem: `http://localhost:8080`.
Swagger UI: `http://localhost:8080/api/swagger-ui.html`

## 3. Mobile (Aplikacja)

### Instalacja zależności
```bash
cd mobile
npm install
```

### Konfiguracja Androida (Ważne!)
Upewnij się, że masz plik `mobile/android/local.properties` wskazujący na Twoje SDK. Jeśli nie, utwórz go:
```properties
sdk.dir=/Users/TWOJ_USER/Library/Android/sdk
```
*(Na Windows ścieżka wygląda inaczej, np. `C:\Users\User\AppData\Local\Android\Sdk`)*

### Uruchomienie (Android)
1.  Otwórz emulator w Android Studio (lub podłącz telefon).
2.  W terminalu (folder `mobile`):
    ```bash
    # Uruchomienie Metro Bundlera + Instalacja aplikacji
    npm start
    # W nowym oknie:
    npm run android
    ```

### Uruchomienie (iOS - tylko macOS)
1.  Zainstaluj zależności (wymaga CocoaPods):
    ```bash
    cd mobile/ios && pod install && cd ..
    ```
2.  Uruchom:
    ```bash
    npm run ios
    ```

## 4. Rozwiązywanie problemów

*   **Port 5432 zajęty:** Jeśli masz lokalnego Postgresa na Macu, wyłącz go: `brew services stop postgresql`.
*   **Aplikacja nie łączy się z API:**
    *   Android Emulator wymaga przekierowania portów: `adb reverse tcp:8080 tcp:8080`
    *   Adres API w `src/constants/config.ts` dla Androida to `10.0.2.2`, dla iOS `localhost`.
*   **Błędy Gradle:** Upewnij się, że używasz Java 17.