# Currency Exchange Mobile

Aplikacja mobilna React Native.

## 🚀 Quick Start

### Instalacja

```bash
npm install
```

### Uruchomienie

```bash
# Android
npm run android

# iOS
npm run ios
```

## 📂 Struktura

```
src/
├── screens/          # Ekrany
├── components/       # Komponenty
├── navigation/       # Nawigacja
├── services/         # API
├── hooks/           # Custom hooks
├── types/           # TypeScript types
├── constants/       # Stałe
└── utils/           # Utilities
```

## 📱 Ekrany (planowane)

- LoginScreen
- RegisterScreen
- HomeScreen
- RatesScreen
- ExchangeScreen
- HistoryScreen
- ProfileScreen

## 🔧 Konfiguracja

Edytuj `src/constants/config.ts` dla URL API.

### Android localhost

```typescript
BASE_URL: 'http://10.0.2.2:8080/api'
```

Lub użyj:
```bash
adb reverse tcp:8080 tcp:8080
```
