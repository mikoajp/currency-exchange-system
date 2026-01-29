# 💱 Currency Exchange System

Mobilny system kantoru wymiany walut z integracją **API NBP** - projekt akademicki.

## 👥 Autorzy
- **Mikołaj Przybysz**
- **Jakub Dyba**

**Przedmiot:** Zagadnienia sieciowe w systemach mobilnych  
**Rok akademicki:** 2024/2025  
**Uczelnia:** Akademia Ekonomiczno-Humanistyczna w Warszawie

---

## 📋 Opis projektu

Aplikacja mobilna do zarządzania portfelem walutowym z funkcją wymiany walut w czasie rzeczywistym. System pobiera **aktualne kursy z API NBP** i automatycznie je synchronizuje.

### ✨ Główne funkcje:
- 🔐 **Rejestracja i logowanie** (JWT Authentication)
- 💰 **Zarządzanie portfelem** multi-walutowym (PLN, USD, EUR, GBP, CHF)
- 💱 **Wymiana walut** w czasie rzeczywistym
- 📊 **Wykresy kursów NBP** - historia kursów dla 4 głównych walut
- 📜 **Historia transakcji** - pełna historia operacji
- 🔄 **Automatyczna synchronizacja** - kursy aktualizowane codziennie o 12:00 z API NBP

---

## 🚀 Szybki Start

### Wymagania
- Java 17+
- Node.js 18+
- Docker Desktop
- Android Studio / Xcode

### 1️⃣ Backend (Spring Boot + PostgreSQL)

```bash
cd backend

# Uruchom PostgreSQL w Docker
docker-compose up -d postgres

# Uruchom backend
./gradlew bootRun
```

✅ **Backend działa na:** http://localhost:8080

### 2️⃣ Mobile (React Native)

```bash
cd mobile

# Zainstaluj zależności
npm install

# Android - przekieruj port backendu
adb reverse tcp:8080 tcp:8080

# Uruchom aplikację
npm run android    # Android
# lub
npm run ios        # iOS (tylko macOS)
```

✅ **Aplikacja uruchomi się automatycznie w emulatorze**

---

## 🏗️ Architektura

```
📦 currency-exchange-system/
├── 🔧 backend/          Spring Boot 3.2 + PostgreSQL + NBP API
├── 📱 mobile/           React Native + TypeScript
├── 📄 docs/             Dokumentacja projektowa
└── 🔄 .github/          CI/CD (GitHub Actions)
```

### Stack technologiczny

| Warstwa | Technologie |
|---------|-------------|
| **Backend** | Java 17, Spring Boot, Spring Security, JWT, JPA, Flyway |
| **Database** | PostgreSQL 15, Docker |
| **Mobile** | React Native, TypeScript, React Navigation, React Query |
| **API** | RESTful API + Integracja z API NBP |
| **DevOps** | Docker Compose, GitHub Actions |

---

## 📊 Funkcjonalności

### Backend (Spring Boot)
- ✅ REST API z JWT Authentication
- ✅ Integracja z API NBP (automatyczny scheduler)
- ✅ Zarządzanie portfelami użytkowników
- ✅ System transakcji z walidacją
- ✅ Cache kursów walut w PostgreSQL
- ✅ Migracje bazy danych (Flyway)

### Mobile (React Native)
- ✅ Intuicyjny interfejs użytkownika
- ✅ Rejestracja i logowanie
- ✅ Przegląd portfeli walutowych
- ✅ Wymiana walut z kalkulatorem
- ✅ **Wykresy kursów NBP** (USD, EUR, GBP, CHF)
- ✅ Historia wszystkich transakcji
- ✅ Doładowanie konta (PayPal)

---

## 📚 Dokumentacja

- [Backend README](./backend/README.md) - API Documentation
- [Mobile README](./mobile/README.md) - App Documentation
- [Dokumentacja projektowa PDF](./docs/Dokumentacja_Projektowa.pdf)

---

## 🔄 Przepływ danych - Kursy NBP

```
┌─────────────┐
│  NBP API    │ api.nbp.pl
└──────┬──────┘
       │ Codziennie 12:00 (pon-pt)
       ▼
┌─────────────────┐
│ Backend Scheduler│ @Scheduled
└────────┬─────────┘
         │ Cache w DB
         ▼
┌──────────────────┐
│   PostgreSQL     │ exchange_rates
└────────┬─────────┘
         │ REST API
         ▼
┌──────────────────┐
│  React Native    │ React Query + Cache
└──────────────────┘
```

---

## 📄 Licencja

Projekt edukacyjny - **Akademia Ekonomiczno-Humanistyczna w Warszawie**
