# Currency Exchange Backend

Spring Boot REST API dla systemu kantoru wymiany walut.

## 🚀 Quick Start

### Uruchomienie

```bash
# PostgreSQL
docker-compose up -d

# Aplikacja
./gradlew bootRun
```

API: http://localhost:8080  
Swagger: http://localhost:8080/api/swagger-ui.html

## 📂 Struktura

```
src/main/java/pl/aeh/currencyexchange/
├── config/           # Konfiguracja (Security, CORS)
├── controller/       # REST Controllers
├── service/          # Business Logic
├── repository/       # JPA Repositories
├── model/           # JPA Entities
├── dto/             # Data Transfer Objects
├── exception/       # Custom Exceptions
└── security/        # JWT, Authentication
```

## 🗄️ Database

- PostgreSQL: localhost:5432
- PgAdmin: http://localhost:5050
  - Email: admin@admin.com
  - Password: admin

## 🧪 Tests

```bash
./gradlew test
```

## 📝 Endpoints (planowane)

- POST /api/auth/register
- POST /api/auth/login
- GET /api/wallets
- POST /api/wallets/deposit
- POST /api/exchange
- GET /api/rates/current
