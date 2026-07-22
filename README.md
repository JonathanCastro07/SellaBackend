# Sella — Backend

Backend REST API for **Sella**, a platform for organizing and managing online raffles ("rifas"). Organizers create raffles and manage buyers; buyers reserve numbers, upload a payment receipt, and get confirmed once the organizer approves the payment.

Live app: [sella-frontend.vercel.app](https://sella-frontend.vercel.app) — frontend repo: [SellaFrontend](https://github.com/JonathanCastro07/SellaFrontend)

## Features

- **JWT authentication** with Spring Security (stateless sessions, role-based access for organizers vs. buyers)
- **Raffle lifecycle management**: create a raffle, auto-generate all available numbers, reserve, confirm/reject payment, mark as drawn
- **Payment receipt uploads** to Cloudinary
- **Transactional email notifications** via the Brevo API (purchase confirmations, etc.)
- **Telegram bot integration** for real-time organizer notifications
- **Scheduled background jobs**:
  - Daily cron job that automatically closes raffles past their draw date
  - Recurring job that releases numbers whose payment deadline expired without confirmation
  - Telegram update polling
- **Global exception handling** for consistent, structured API error responses
- **API documentation** with Swagger / OpenAPI
- **Dockerized** for deployment

## Tech Stack

Java · Spring Boot · Spring Security · Spring Data JPA · PostgreSQL (production) / H2 (tests) · JWT (jjwt) · Docker · Cloudinary · Brevo · Telegram Bot API · Swagger/OpenAPI · JUnit 5 · AssertJ

## API Overview

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Authenticate and receive a JWT |
| GET | `/api/auth/perfil` | Get the authenticated user's profile |
| POST | `/api/rifas` | Create a new raffle (auto-generates all numbers) |
| GET | `/api/rifas/{id}` | View a raffle's public board |
| GET | `/api/rifas/mias` | List the authenticated organizer's raffles |
| GET | `/api/rifas/{id}/organizador` | View a raffle with buyer/payment detail (organizer only) |
| POST | `/api/rifas/{id}/marcar-sorteada` | Mark a closed raffle as drawn with a winning number |
| POST | `/api/rifas/{rifaId}/numeros/{numero}/apartar` | Reserve a number |
| POST | `/api/rifas/{rifaId}/numeros/{numero}/comprobante` | Upload a payment receipt |
| POST | `/api/rifas/{rifaId}/numeros/{numero}/confirmar` | Confirm payment (organizer only) |
| POST | `/api/rifas/{rifaId}/numeros/{numero}/rechazar` | Reject payment and release the number |

Full interactive documentation is available at `/swagger-ui.html` once the app is running.

## Running locally

### Prerequisites

- Java 17+
- Maven (or use the included `mvnw`)
- A PostgreSQL database (or adjust `application.properties` to use H2 for local dev)

### Environment variables

The app is configured entirely through environment variables — no secrets are committed to the repo:

```
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
JWT_EXPIRATION=86400000        # optional, defaults to 24h
TELEGRAM_BOT_TOKEN=
TELEGRAM_CHAT_ID=
BREVO_API_KEY=
BREVO_SENDER_EMAIL=            # optional
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
```

### Run

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### Run with Docker

```bash
docker build -t sella-backend .
docker run -p 8080:8080 --env-file .env sella-backend
```

### Run tests

```bash
./mvnw test
```

## Project Structure

```
src/main/java/com/JonathanCastro07/Sella/
├── controller/     # REST endpoints
├── service/        # Business logic, scheduled jobs, email/Telegram integrations
├── modelo/         # JPA entities
├── repository/     # Spring Data repositories
├── DTOs/           # Request/response objects
├── security/       # JWT filter and service
├── config/         # Security and Cloudinary configuration
└── Exception/      # Custom exceptions and global exception handler
```
