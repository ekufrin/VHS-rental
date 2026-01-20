# 🎬 VHS Rental System

A modern RESTful API service for managing VHS tape rentals, built with Spring Boot 4 and Java 25. This application provides a complete rental management system with user authentication, inventory tracking, and review functionality.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Setup & Installation](#-setup--installation)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Environment Variables](#-environment-variables)
- [Database Migrations](#-database-migrations)
- [Additional Notes]("#-additional-notes")

---

## ✨ Features

### Core Functionality
- **User Management**
  - User registration and JWT-based authentication
  - Secure password hashing with BCrypt
  - User profile with favorite genres
  
- **VHS Inventory Management**
  - CRUD operations for VHS tapes
  - Image upload support (JPEG/PNG, max 10MB)
  - Genre categorization
  - Stock level tracking
  - Rental price management

- **Rental System**
  - Create new rentals with automatic availability checking
  - Finish rentals with automatic price calculation
  - Late fee calculation (10% per day overdue)
  - Optimistic locking to prevent concurrent rental conflicts
  - Rental history tracking

- **Review System**
  - Users can review rented VHS tapes
  - Rating and comment functionality
  - Reviews linked to specific rentals
  - Update and delete own reviews

### Technical Features
- **Security**: JWT authentication with configurable expiration
- **API Documentation**: Auto-generated OpenAPI 3.0 specification with Swagger UI
- **Database**: PostgreSQL with Flyway migrations
- **Data Mapping**: MapStruct for DTO conversions
- **Logging**: Logback with file-based logging
- **Pagination**: Customizable pagination for all list endpoints
- **Docker Support**: Full containerization with Docker Compose

---

## 🛠 Tech Stack

- **Java 25**
- **Spring Boot 4.0.1**
  - Spring Data JPA
  - Spring Security
  - Spring Web MVC
  - Spring Validation
- **PostgreSQL** (Database)
- **Flyway** (Database migrations)
- **JWT** (Authentication - jjwt 0.13.0)
- **MapStruct** (Object mapping)
- **Lombok** (Boilerplate reduction)
- **SpringDoc OpenAPI** (API documentation)
- **Docker & Docker Compose** (Containerization)

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 25** or higher
- **Maven 3.9+**
- **Docker** and **Docker Compose**
- **Git**

---

## 🚀 Setup & Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd "VHS Rental"
```

### 2. Configure Environment Variables

Create a `.env` file in the project root directory with the following variables:

```env
# Database Configuration
DB_USER=your_database_username
DB_PASSWORD=your_database_password
DB_HOST=localhost
DB_NAME=vhs

# API Configuration
API_PREFIX=/api/v1

# JWT Configuration (must be at least 256 bits / 64 hex characters)
JWT_SECRET=your_jwt_secret_key_at_least_64_hex_characters_long
```

> **⚠️ Important Security Notes:**
> - **JWT_SECRET**: Must be at least 256 bits (64 hexadecimal characters). Generate a secure key using:
>   ```bash
>   openssl rand -hex 32
>   ```

### 3. Build the Application

```bash
./mvnw clean package
```

---

## 🏃 Running the Application

### Option 1: Docker Compose (Recommended)

This will start both the application and PostgreSQL database in containers:

```bash
docker-compose up --build
```

The application will be available at: `http://localhost:8080/api/v1`

### Option 2: Local Development

1. **Start PostgreSQL** (if not using Docker):
   ```bash
   docker-compose up postgres
   ```
   Or use your local PostgreSQL instance on port 5433.

2. **Run the Spring Boot application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### Stopping the Application

```bash
docker-compose down
```

To remove volumes (database data):
```bash
docker-compose down -v
```

---

## 📖 API Documentation

### Swagger UI

Once the application is running, access the interactive API documentation:

**URL**: [http://localhost:8080/api/v1/swagger-ui/index.html](http://localhost:8080/api/v1/swagger-ui/index.html)

### OpenAPI Specification

**JSON Format**: [http://localhost:8080/api/v1/v3/api-docs](http://localhost:8080/api/v1/v3/api-docs)

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Login and receive JWT access token + refresh token (in httpOnly cookie)
- `POST /api/v1/auth/refresh-token` - Refresh both access and refresh tokens using existing refresh token
- `POST /api/v1/auth/access-token` - Generate new access token from refresh token
- `POST /api/v1/auth/logout` - Logout and invalidate refresh token

#### VHS Management
- `GET /api/v1/vhs` - List all VHS tapes (paginated)
- `GET /api/v1/vhs/{id}` - Get VHS by ID
- `POST /api/v1/vhs` - Create new VHS (requires auth, supports multipart/form-data for image upload)

#### Rentals
- `POST /api/v1/rentals` - Create a new rental (requires auth)
- `GET /api/v1/rentals` - List all rentals (requires auth, paginated)
- `GET /api/v1/rentals/{id}` - Get rental by ID (requires auth)
- `PATCH /api/v1/rentals/{id}/finish` - Finish a rental and calculate price (requires auth)

#### Reviews
- `POST /api/v1/reviews` - Create a review (requires auth)
- `GET /api/v1/reviews/vhs/{vhsId}` - Get all reviews for a VHS (paginated)
- `GET /api/v1/reviews/{id}` - Get review by ID
- `PUT /api/v1/reviews/{id}` - Update review (requires auth)
- `DELETE /api/v1/reviews/{id}` - Delete review (requires auth)

#### Genres
- `GET /api/v1/genres` - List all genres
- `POST /api/v1/genres` - Create a new genre (requires auth)

---

## 📁 Project Structure

```
VHS Rental/
├── src/
│   ├── main/
│   │   ├── java/com/ekufrin/vhsrental/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── exception/        # Custom exceptions & global handler
│   │   │   ├── genre/            # Genre domain
│   │   │   ├── rental/           # Rental domain
│   │   │   ├── review/           # Review domain
│   │   │   ├── security/         # Security & JWT configuration
│   │   │   ├── user/             # User domain
│   │   │   ├── vhs/              # VHS domain
│   │   │   └── VhsRentalApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── logback-spring.xml
│   │       ├── db/migration/     # Flyway SQL migrations
│   │       └── static/uploads/   # VHS image uploads
│   └── test/                     # Test files
├── docker-compose.yml            # Docker Compose configuration
├── Dockerfile                    # Application Dockerfile
├── pom.xml                       # Maven dependencies
├── .env                          # Environment variables (not in repo)
└── README.md                     # This file
```

---

## 🔐 Environment Variables

| Variable       | Description                                      | Example                          | Required |
|----------------|--------------------------------------------------|----------------------------------|----------|
| `DB_USER`      | PostgreSQL database username                     | `kufrin`                         | ✅       |
| `DB_PASSWORD`  | PostgreSQL database password                     | `YourSecurePassword123`          | ✅       |
| `DB_HOST`      | Database host (use `postgres` in Docker)         | `localhost` or `postgres`        | ✅       |
| `DB_NAME`      | Database name                                    | `vhs`                            | ✅       |
| `API_PREFIX`   | API base path prefix                             | `/api/v1`                        | ✅       |
| `JWT_SECRET`   | Secret key for JWT token signing (min 256 bits) | `db62404d931017374...` (64 chars)| ✅       |

---

## 🗄️ Database Migrations

The application uses **Flyway** for database version control. Migrations are automatically applied on application startup.

### Migration Files Location
```
src/main/resources/db/migration/
```

### Migration Naming Convention
```
V{version}__{description}.sql
```

Example: `V1__init.sql`

### Manual Migration Control

To run migrations manually:
```bash
./mvnw flyway:migrate
```

To view migration status:
```bash
./mvnw flyway:info
```

---

## 🧪 Testing

Run tests with Maven:
```bash
./mvnw test
```

---

## 📝 Additional Notes

### Image Upload
- Supported formats: JPEG, PNG
- Maximum file size: 10 MB
- Images are stored in: `src/main/resources/static/uploads/`
- Image URLs are automatically generated and included in VHS responses

### Rental Price Calculation
- Base price: `rentalPrice * numberOfDays`
- Late fee: `10% of daily rate * daysLate`
- Prices are rounded to 2 decimal places

### Authentication
- **Access Token**: Short-lived JWT token (expires after 15 minutes, configurable in `application.properties`)
- **Refresh Token**: Long-lived token stored in httpOnly cookie (expires after 7 days)
- Include access token in request header: `Authorization: Bearer <access_token>`
- **Token Refresh Flow**:
  1. Use `/auth/refresh-token` to get both new access and refresh tokens
  2. Use `/auth/access-token` to get only a new access token (keeps same refresh token)
- **Logout**: Call `/auth/logout` to invalidate refresh token and clear cookie
- Refresh token is automatically sent via httpOnly cookie (secure, protected from XSS attacks)

### Pagination
- Default page size: 20 items
- Maximum page size: 100 items
- Page numbers are 1-indexed

---
