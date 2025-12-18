# 💱 Currency Exchange System

System mobilny kantoru wymiany walut - projekt akademicki

## 👥 Autorzy
- **Mikołaj Przybysz**
- **Jakub Dyba**

**Przedmiot:** Zagadnienia sieciowe w systemach mobilnych  
**Rok akademicki:** 2024/2025  
**Uczelnia:** Akademia Ekonomiczno-Humanistyczna w Warszawie

## 📋 Opis projektu

Mobilny system kantoru wymiany walut z integracją API NBP, umożliwiający:
- Rejestrację i autentykację użytkowników (JWT)
- Zarządzanie wirtualnym portfelem walutowym
- Wymianę walut w czasie rzeczywistym
- Przeglądanie historii transakcji
- Dostęp do aktualnych i historycznych kursów walut

## 🏗️ Architektura

```
currency-exchange-system/
├── backend/          # Spring Boot REST API
├── mobile/           # React Native App
└── docs/            # Dokumentacja projektu
```

### Stack technologiczny

**Backend:**
- Java 17 / Spring Boot 3.2+
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL 15+
- Flyway (migracje DB)

**Mobile:**
- React Native + TypeScript
- React Navigation
- Axios + React Query
- AsyncStorage

**Infrastructure:**
- Docker & Docker Compose
- GitHub Actions (CI/CD)

## 🚀 Quick Start

### Wymagania
- JDK 17+
- Node.js 18+
- Docker Desktop
- Git

### Backend
```bash
cd backend
docker-compose up -d  # PostgreSQL
./gradlew bootRun
```

API: http://localhost:8080

### Mobile
```bash
cd mobile
npm install
npm run android  # lub ios
```

## 📚 Dokumentacja

- [Backend README](./backend/README.md)
- [Mobile README](./mobile/README.md)
- [Dokumentacja projektowa](./docs/Dokumentacja_Projektowa.pdf)
- [Zadanie projektowe](./docs/Projekt.pdf)

## 📊 Status projektu

| Faza | Status |
|------|--------|
| Setup środowiska | ✅ Done |
| Autentykacja | 📋 Planned |
| Integracja NBP | 📋 Planned |
| Moduł wymiany | 📋 Planned |
| UI Mobile | 📋 Planned |

## 📄 Licencja

Projekt edukacyjny - AEH Warszawa
