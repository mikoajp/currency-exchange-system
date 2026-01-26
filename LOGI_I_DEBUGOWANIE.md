# 📋 Logi i Debugowanie - Kompletny Przewodnik

## 🎯 Szybki Start

### Najprostszy sposób - React Native Logs

```bash
cd mobile
npx react-native log-android
```

To pokaże wszystkie `console.log()`, `console.error()`, `console.warn()` z Twojego kodu React Native.

---

## 📱 4 Sposoby na Logi Mobile

### 1️⃣ React Native Logger (ZALECANY dla JavaScript)

**Co pokazuje:** Wszystkie console.log/error/warn z kodu React Native

```bash
cd mobile
npx react-native log-android

# Możesz dodać alias do package.json:
# "log": "react-native log-android"
# Wtedy: npm run log
```

**Przykład wyjścia:**
```
 LOG  [AuthContext] Sprawdzanie tokenu...
 LOG  [AuthContext] Token znaleziony: eyJhbGc...
 LOG  [WalletScreen] Pobieranie portfeli...
 LOG  [ExchangeRateService] Pobieranie historii USD 30d
 ERROR [API] Błąd połączenia: Network Error
```

---

### 2️⃣ ADB Logcat (Wszystkie logi systemowe)

**Co pokazuje:** Cały Android system log + aplikacja

```bash
# Wszystkie logi (DUŻO!)
adb logcat

# Tylko React Native:
adb logcat | grep ReactNative

# Tylko Twoja aplikacja:
adb logcat | grep com.mobile

# Z kolorami i timestampem:
adb logcat -v time | grep com.mobile

# Zapisz do pliku:
adb logcat > logs.txt
```

**Filtry przydatne:**
```bash
# Tylko błędy:
adb logcat *:E

# Tylko React Native i błędy:
adb logcat ReactNative:* *:E

# Konkretny tag:
adb logcat -s ReactNativeJS
```

---

### 3️⃣ Metro Bundler Logs

**Co pokazuje:** Kompilacja bundle'a, hot reload, błędy transformacji

To okno terminala gdzie uruchomiłeś `npm start`. Pokazuje:
- Bundle compilation
- Fast Refresh events
- Transform errors
- Module resolution errors

**Jeśli zamknąłeś okno Metro:**
```bash
cd mobile
npm start

# Lub z czyszczeniem cache:
npm start -- --reset-cache
```

**Przykład wyjścia:**
```
 BUNDLE  ./index.js ░░░░░░░░░░░░░░░░ 100.0% (1234/1234)
 LOG  Running "CurrencyExchangeMobile" with {"rootTag":11}
 WARN  Fast Refresh took 234ms
```

---

### 4️⃣ Chrome DevTools (NAJLEPSZY do debugowania!)

**Co daje:**
- 🔍 Pełny debugger z breakpointami
- 🔎 Inspekcja zmiennych
- 📊 Network tab (żądania HTTP)
- 💾 Console interaktywny
- ⚡ Profile wydajności

**Jak uruchomić:**

**Metoda A: Przez Dev Menu**
1. Na emulatorze naciśnij `Cmd/Ctrl + M`
2. Wybierz **"Debug"**
3. Chrome otworzy się automatycznie z `http://localhost:8081/debugger-ui`

**Metoda B: Potrząśnięcie**
1. Na emulatorze: Device → Shake
2. Wybierz "Debug"

**Metoda C: Ręcznie**
```bash
# Otwórz w przeglądarce:
open http://localhost:8081/debugger-ui
```

**Co zobaczysz w Chrome:**
```
Sources:
  └─ webpack://
      └─ src/
          ├─ screens/
          ├─ services/
          └─ context/

Console:
> console.log('test')
< undefined

Network:
  GET /api/wallets/me    200 OK  123ms
  GET /api/rates/current 200 OK  45ms
```

**Ustawienie breakpointu:**
1. Otwórz Sources tab
2. Znajdź plik np. `ExchangeRateChartsScreen.tsx`
3. Kliknij na numer linii → czerwona kropka
4. W aplikacji wykonaj akcję
5. Debugger zatrzyma się na breakpoincie!

---

## 🔧 Debugowanie Specyficznych Rzeczy

### Network Requests (API Calls)

**Sposób 1: Chrome DevTools Network Tab**
```
1. Cmd/Ctrl+M → Debug
2. W Chrome → F12 → Network
3. W aplikacji wykonaj API call
4. Zobacz request/response w Chrome
```

**Sposób 2: Axios Interceptor**
```typescript
// mobile/src/services/api.ts
apiClient.interceptors.request.use(request => {
  console.log('🚀 Request:', request.method, request.url);
  return request;
});

apiClient.interceptors.response.use(
  response => {
    console.log('✅ Response:', response.config.url, response.status);
    return response;
  },
  error => {
    console.error('❌ Error:', error.config?.url, error.message);
    return Promise.reject(error);
  }
);
```

**Sposób 3: React Query DevTools**
```bash
npm install @tanstack/react-query-devtools

// W App.tsx
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

// W komponencie:
{__DEV__ && <ReactQueryDevtools />}
```

---

### Redux/Context State

```typescript
// W AuthContext.tsx
useEffect(() => {
  console.log('🔐 Auth State Changed:', {
    userToken: userToken ? 'EXISTS' : 'NULL',
    isLoading
  });
}, [userToken, isLoading]);
```

---

### Navigation

```typescript
// Dodaj listener w App.tsx
const navigationRef = React.useRef();

<NavigationContainer
  ref={navigationRef}
  onStateChange={(state) => {
    const currentRoute = state?.routes[state.index]?.name;
    console.log('📍 Navigation:', currentRoute);
  }}
>
```

---

### Render Performance

```typescript
// Dodaj do komponentu który chcesz monitorować
useEffect(() => {
  console.log('🔄 Component rendered:', Date.now());
});

// Lub sprawdź ile razy renderuje:
const renderCount = useRef(0);
renderCount.current++;
console.log('🔢 Render count:', renderCount.current);
```

---

## 🐛 Typowe Problemy i Debugowanie

### Problem: "Białe/czerwone ekrany"

**Debugowanie:**
```bash
# 1. Sprawdź Metro logs
# Szukaj błędów kompilacji

# 2. Sprawdź React Native logs
npx react-native log-android

# 3. Czerwony ekran na emulatorze
# Przeczytaj komunikat błędu!
# Często pokazuje dokładny plik i linię
```

---

### Problem: "API nie odpowiada"

**Debugowanie:**
```bash
# 1. Sprawdź czy backend działa
curl http://localhost:8080/api/rates/current

# 2. Sprawdź config
cat mobile/src/constants/config.ts

# 3. Dla emulatora Android użyj 10.0.2.2:
API_BASE_URL: 'http://10.0.2.2:8080/api'

# 4. Zobacz network w Chrome DevTools
Cmd+M → Debug → Chrome F12 → Network
```

**Dodaj logi do API service:**
```typescript
// mobile/src/services/api.ts
console.log('🌐 API Base URL:', APP_CONFIG.API_BASE_URL);

try {
  const response = await apiClient.get('/rates/current');
  console.log('✅ Rates fetched:', response.data.length);
} catch (error) {
  console.error('❌ API Error:', error.message);
  console.error('📍 URL:', error.config?.url);
  console.error('🔧 Config:', error.config?.baseURL);
}
```

---

### Problem: "Wykresy nie ładują się"

**Debugowanie:**
```typescript
// mobile/src/screens/ExchangeRateChartsScreen.tsx

// Dodaj logi w useQuery:
const { data: rates, isLoading, error } = useQuery({
  queryKey: ['exchangeRates', selectedCurrency, timeRange],
  queryFn: async () => {
    console.log('📊 Fetching charts:', selectedCurrency, timeRange);
    try {
      const result = await exchangeRateService.getHistoricalRates(
        selectedCurrency, 
        timeRange
      );
      console.log('✅ Charts data:', result.length, 'points');
      return result;
    } catch (err) {
      console.error('❌ Charts error:', err);
      throw err;
    }
  },
});

// Sprawdź stan:
console.log('📊 Chart State:', { 
  isLoading, 
  hasData: !!rates, 
  dataLength: rates?.length,
  error: error?.message 
});
```

---

## 📊 Zaawansowane: Flipper (Facebook's Mobile Debugger)

**Co to:** Zaawansowany debugger mobilny od Facebooka

**Instalacja:**
```bash
brew install --cask flipper  # Mac
# lub pobierz: https://fbflipper.com/
```

**Funkcje:**
- Network inspector
- Layout inspector
- Redux/MobX state
- Database inspector
- Crash reporter
- Performance monitor

**Integracja z React Native:**
```bash
cd mobile
npx react-native doctor
# Sprawdzi czy Flipper jest skonfigurowany
```

---

## 📝 Best Practices - Logowanie

### ✅ DOBRE praktyki:

```typescript
// 1. Używaj prefixów dla kontekstu
console.log('[AuthService] Logowanie użytkownika...');
console.log('[API] Pobieranie kursów...');

// 2. Strukturyzowane logi
console.log('User logged in:', { 
  email: user.email, 
  role: user.role,
  timestamp: new Date().toISOString()
});

// 3. Różne poziomy
console.log('ℹ️ Info: Akcja wykonana');
console.warn('⚠️ Warning: Coś nietypowego');
console.error('❌ Error:', error.message);

// 4. __DEV__ guard dla drogich operacji
if (__DEV__) {
  console.log('Debug info:', JSON.stringify(bigObject, null, 2));
}
```

### ❌ ZŁE praktyki:

```typescript
// NIE rób tego:
console.log(bigObject);  // Zrzuca gigantyczny obiekt
console.log('test');     // Bez kontekstu
console.log(error);      // Pokaż error.message!
// Produkcja z console.log (usuń przed buildem!)
```

---

## 🚀 Quick Commands Cheatsheet

```bash
# LOGI
npx react-native log-android          # React Native logs
adb logcat | grep com.mobile          # Android system logs
npm start                             # Metro bundler logs

# DEBUGOWANIE
Cmd/Ctrl + M → Debug                  # Chrome DevTools
Cmd/Ctrl + M → Reload                 # Przeładuj app
Cmd/Ctrl + M → Enable Fast Refresh    # Auto-reload

# RESTART
npm start -- --reset-cache            # Czyści cache Metro
adb shell pm clear com.mobile         # Czyści dane app
adb uninstall com.mobile              # Odinstaluj app

# DEVICE
adb devices                           # Lista urządzeń
adb shell dumpsys window | grep Focus # Aktywna aplikacja
adb shell screenrecord /sdcard/d.mp4  # Nagrywanie ekranu
```

---

## 💡 Pro Tips

1. **Trzymaj Metro logs zawsze otwarte** - często pierwszy znak problemu
2. **Używaj Chrome DevTools** - network tab jest nieoceniony
3. **Dodaj console.log strategicznie** - nie wszędzie, tylko kluczowe miejsca
4. **Sprawdź React Query DevTools** - zobacz cache i stany zapytań
5. **Flipper dla poważnego debugowania** - jest wart nauki

---

## 🆘 Szybka Pomoc

**Aplikacja się crashuje:**
```bash
adb logcat | grep FATAL
```

**Nie widać logów:**
```bash
npx react-native log-android --verbose
```

**Metro nie startuje:**
```bash
npx react-native start --reset-cache --port 8082
```

**Dziwne błędy po pull:**
```bash
cd mobile
rm -rf node_modules
npm install
cd ios && pod install && cd ..
npm start -- --reset-cache
```

---

Teraz masz pełną kontrolę nad logami i debugowaniem! 🎉
