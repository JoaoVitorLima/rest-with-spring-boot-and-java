<div align="center">

# 🚀 RESTful API with Spring Boot & Java

### A production-grade REST API built with Java 21, Spring Boot 3, and industry best practices

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-9.1-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)

</div>

---

## 📖 About This Project

This is a **comprehensive RESTful API** built from the ground up using Java and Spring Boot. It goes well beyond a simple CRUD — it demonstrates real-world patterns and integrations that mirror what you'd find in enterprise production systems: JWT-based authentication, multi-format content negotiation, file export/import pipelines, email dispatch, QR code generation, paginated HATEOAS responses, and a full test suite backed by Testcontainers.

> 💡 Every technology used here was chosen deliberately. The sections below explain **what** each feature does and **why** it was implemented that way.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Features Deep Dive](#-features-deep-dive)
  - [JWT Authentication & Security](#1-jwt-authentication--security)
  - [RESTful APIs with Content Negotiation](#2-restful-apis-with-content-negotiation)
  - [HATEOAS — Hypermedia-Driven Responses](#3-hateoas--hypermedia-driven-responses)
  - [Pagination & Sorting](#4-pagination--sorting)
  - [File Upload & Download](#5-file-upload--download)
  - [Data Export (PDF, XLSX, CSV)](#6-data-export-pdf-xlsx-csv)
  - [Bulk Import (XLSX, CSV)](#7-bulk-import-xlsx-csv)
  - [PDF Reports with JasperReports & QR Codes](#8-pdf-reports-with-jasperreports--qr-codes)
  - [Email Service with Attachments](#9-email-service-with-attachments)
  - [Database Migrations with Flyway](#10-database-migrations-with-flyway)
  - [OpenAPI / Swagger Documentation](#11-openapi--swagger-documentation)
  - [Global Exception Handling](#12-global-exception-handling)
  - [Testing — Unit, Integration & Testcontainers](#13-testing--unit-integration--testcontainers)
  - [Containerization with Docker](#14-containerization-with-docker)
- [API Endpoints](#-api-endpoints)
- [Running Locally](#-running-locally)
- [Project Structure](#-project-structure)

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Security | Spring Security + JWT (Auth0 java-jwt 4.4) |
| Persistence | Spring Data JPA + Hibernate + MySQL 9.1 |
| Database Migrations | Flyway |
| API Documentation | SpringDoc OpenAPI 3 / Swagger UI |
| Hypermedia | Spring HATEOAS |
| Serialization | Jackson (JSON, XML, YAML) |
| File Formats | Apache POI (XLSX), Apache Commons CSV, JasperReports (PDF) |
| QR Code Generation | ZXing (Google) 3.5.3 |
| Email | Spring Boot Mail (SMTP / Gmail) |
| Object Mapping | Dozer Mapper 7 |
| Testing | JUnit 5, Mockito, REST Assured, Testcontainers |
| Containerization | Docker + Docker Compose |
| Build Tool | Maven |

---

## 🔍 Features Deep Dive

### 1. JWT Authentication & Security

**What it does:** Every API endpoint is protected behind stateless JWT (JSON Web Token) authentication. Users sign in with credentials and receive an access token (1 hour) and a refresh token (3 hours). The refresh token allows obtaining a new access token without re-entering credentials.

**Why I built it this way:**
- **Stateless sessions** (no server-side session storage) make the API horizontally scalable.
- **PBKDF2 with HMAC-SHA256** password hashing ensures passwords are stored securely even if the database is compromised.
- The `JwtTokenFilter` intercepts every request, validates the Bearer token, and populates the Spring Security context — cleanly separating auth concerns from business logic.
- Public routes (`/auth/signin`, `/auth/refresh/**`, Swagger UI) are explicitly whitelisted, everything else requires authentication.

**Key files:**
- [`SecurityConfig.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/config/SecurityConfig.java) — filter chain, PBKDF2 encoder, stateless session policy
- [`JwtTokenProvider.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/security/jwt/JwtTokenProvider.java) — token creation, validation, and refresh logic
- [`JwtTokenFilter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/security/jwt/JwtTokenFilter.java) — per-request token resolution
- [`AuthController.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/controllers/AuthController.java) — sign-in, refresh, and user creation endpoints

---

### 2. RESTful APIs with Content Negotiation

**What it does:** All endpoints support **three media types** — `application/json`, `application/xml`, and `application/yaml` — through the `Accept` request header. The same endpoint returns the correct format automatically.

**Why I built it this way:**
- Content negotiation is a core REST principle that makes an API truly client-agnostic.
- A custom `YamlJackson2HttpMessageConverter` was registered to add YAML support that Spring doesn't provide out of the box.
- This demonstrates understanding of how Spring's `HttpMessageConverter` pipeline works.

**Key files:**
- [`WebConfig.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/config/WebConfig.java) — content negotiation configuration + CORS
- [`YamlJackson2HttpMessageConverter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/serialization/converter/YamlJackson2HttpMessageConverter.java) — custom YAML converter

---

### 3. HATEOAS — Hypermedia-Driven Responses

**What it does:** Every `PersonDTO` and `BookDTO` returned by the API includes embedded **hypermedia links** pointing to all related actions (find, create, update, disable, delete, export). This is the HATEOAS (Hypermedia as the Engine of Application State) constraint of REST.

**Why I built it this way:**
- HATEOAS makes an API self-describing. A client doesn't need out-of-band documentation to discover what it can do next — the response tells it.
- Paginated list responses include navigation links (`next`, `prev`, `first`, `last`) automatically via `PagedResourcesAssembler`.
- This is what distinguishes a Level 3 (mature) REST API from a simple HTTP/JSON service.

**Key files:**
- [`PersonService.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/services/PersonService.java) — `addHateoasLinks()` and `buildPagedModel()`
- [`PersonDTO.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/data/dto/PersonDTO.java) — extends `RepresentationModel`

---

### 4. Pagination & Sorting

**What it does:** List endpoints (`GET /api/person/v1`, `GET /api/book/v1`) support `page`, `size`, and `direction` query parameters, returning a properly shaped `PagedModel` with metadata and navigation links.

**Why I built it this way:**
- Returning unbounded lists is a serious performance and scalability anti-pattern. Pagination is mandatory in any production API.
- Spring Data's `Pageable` abstraction makes this clean and database-agnostic.
- A custom name-search endpoint (`/findPeopleByName/{firstName}`) demonstrates paginated full-text search using a JPQL `LIKE` query.

---

### 5. File Upload & Download

**What it does:** The `FileController` exposes endpoints to upload a single or multiple files (stored on the server filesystem) and download them back by filename. It returns a structured `UploadFileResponseDTO` including the download URI.

**Why I built it this way:**
- File handling is a very common real-world requirement and demonstrates understanding of `MultipartFile`, filesystem I/O, and `Resource` as a response body.
- Configurable upload directory and size limits (`max-file-size: 200MB`) are externalized to `application.yml`.

**Key files:**
- [`FileController.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/controllers/FileController.java)
- [`FileStorageService.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/services/FileStorageService.java)

---

### 6. Data Export (PDF, XLSX, CSV)

**What it does:** The `GET /api/person/v1/exportPage` endpoint streams a paginated list of people as a downloadable file. The format is determined by the `Accept` header — send `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` for XLSX, `text/csv` for CSV, or `application/pdf` for PDF.

**Why I built it this way:**
- The **Factory Pattern** (`FileExporterFactory`) selects the correct exporter implementation at runtime based on the media type, keeping each exporter isolated and adding new formats trivially.
- The **Strategy / Interface pattern** (`PersonExporter`) means all exporters share the same contract — the service never knows which format it's writing.
- This mirrors how enterprise reporting pipelines are structured in real applications.

**Key files:**
- [`FileExporterFactory.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/exporter/factory/FileExporterFactory.java)
- [`CsvExporter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/exporter/impl/CsvExporter.java), [`XlsxExporter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/exporter/impl/XlsxExporter.java), [`PdfExporter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/exporter/impl/PdfExporter.java)

---

### 7. Bulk Import (XLSX, CSV)

**What it does:** `POST /api/person/v1/massCreation` accepts an uploaded XLSX or CSV file and bulk-inserts all rows into the database, returning the created records with HATEOAS links.

**Why I built it this way:**
- Mass data ingestion from spreadsheets is a very common enterprise requirement (data migration, CRM imports, HR uploads, etc.).
- The same Factory + Strategy pattern used for export is mirrored here for import (`FileImporterFactory`, `FileImporter` contract), demonstrating consistent design thinking.

**Key files:**
- [`FileImporterFactory.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/importer/factory/FileImporterFactory.java)
- [`CsvImporter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/importer/impl/CsvImporter.java), [`XlsxImporter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/importer/impl/XlsxImporter.java)

---

### 8. PDF Reports with JasperReports & QR Codes

**What it does:** `GET /api/person/v1/export/{id}` generates a rich PDF report for a single person using a **JasperReports template** (`.jrxml`). The report includes a sub-report listing the person's associated books and an **embedded QR Code** image pointing to the person's Wikipedia profile URL.

**Why I built it this way:**
- JasperReports is the industry standard for professional report generation in Java. Knowing it is a differentiator.
- Sub-reports (`books.jrxml` nested inside `person.jrxml`) demonstrate the use of hierarchical report templates.
- QR code generation with **ZXing** is embedded directly in the PDF pipeline — the image is generated in memory (`ByteArrayInputStream`) and injected as a report parameter, avoiding any disk I/O.

**Key files:**
- [`PdfExporter.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/file/exporter/impl/PdfExporter.java)
- [`QRCodeService.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/services/QRCodeService.java)
- [`person.jrxml`](rest-with-spring-boot-and-java/src/main/resources/templates/person.jrxml), [`books.jrxml`](rest-with-spring-boot-and-java/src/main/resources/templates/books.jrxml)

---

### 9. Email Service with Attachments

**What it does:** Two email endpoints — one for plain text emails and one for emails with a file attachment. Both are backed by Gmail's SMTP server via Spring Boot Mail.

**Why I built it this way:**
- Transactional email is a standard requirement for any real-world application (notifications, reports, confirmations).
- The `EmailSender` is implemented as a **fluent builder** (`to(...).withSubject(...).withMessage(...).attach(...).send()`), making the sending logic readable and avoiding telescoping constructor calls.
- SMTP credentials are injected from environment variables (`${EMAIL_USERNAME}`, `${EMAIL_PASSWORD}`), which is the correct practice — never hardcoded secrets.

**Key files:**
- [`EmailController.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/controllers/EmailController.java)
- [`EmailService.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/services/EmailService.java)
- [`EmailSender.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/mail/EmailSender.java)

---

### 10. Database Migrations with Flyway

**What it does:** The entire database schema — all 18 versions from initial table creation to user permissions — is managed by **Flyway versioned migrations**. On startup, Flyway automatically applies any pending scripts in order.

**Why I built it this way:**
- Flyway eliminates the "works on my machine" database state problem. Every environment (dev, test, prod) runs the exact same schema history.
- `ddl-auto: none` means Hibernate never touches the schema — Flyway is the single source of truth.
- This is the standard approach in professional Java projects. It makes schema changes trackable in version control just like code.

**Key files:**
- [`db/migration/`](rest-with-spring-boot-and-java/src/main/resources/db/migration/) — 18 versioned SQL scripts (V1 through V18)

---

### 11. OpenAPI / Swagger Documentation

**What it does:** The full API is documented with **Swagger UI** accessible at the root URL. Every endpoint, request body, and response is annotated using OpenAPI 3 annotations (`@Operation`, `@ApiResponse`, `@Tag`). Controller documentation interfaces (`*ControllerDocs`) separate the annotation boilerplate from the implementation code.

**Why I built it this way:**
- Swagger UI lets any developer (or recruiter!) explore and test every endpoint interactively without writing a single line of client code.
- The **Docs interface pattern** (e.g. `PersonControllerDocs`) keeps the controller class itself clean. The controller implements the interface; all Swagger annotations live there. This is a clean architecture choice.

**Key files:**
- [`OpenApiConfig.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/config/OpenApiConfig.java)
- [`controllers/docs/`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/controllers/docs/) — documentation interfaces for all controllers

---

### 12. Global Exception Handling

**What it does:** A `@ControllerAdvice` class intercepts every exception thrown in the application and maps it to a structured JSON error response with a timestamp, message, and path — instead of leaking stack traces or Spring's default error page.

**Why I built it this way:**
- Consistent, structured error responses are a requirement for any API that will be consumed by real clients.
- Specific exception types (`ResourceNotFoundException → 404`, `InvalidJwtAuthenticationException → 403`, `BadRequestlException → 400`) provide meaningful HTTP status codes rather than a blanket 500.

**Key files:**
- [`CustomEntityResponseHandler.java`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/exception/handler/CustomEntityResponseHandler.java)
- [`exception/`](rest-with-spring-boot-and-java/src/main/java/br/com/erudio/exception/) — custom exception types

---

### 13. Testing — Unit, Integration & Testcontainers

**What it does:** The test suite has three layers:
- **Unit tests** — service layer tested with Mockito mocks, no Spring context needed.
- **Integration tests** — full `@SpringBootTest` tests for every controller endpoint using **REST Assured**, covering JSON, XML, and YAML content types, as well as CORS validation.
- **Testcontainers** — integration tests spin up a real **MySQL 9.1 Docker container** automatically, so tests run against a real database — not H2 or mocks.

**Why I built it this way:**
- Testing against a real database (Testcontainers) eliminates a whole class of "passes in tests, breaks in prod" bugs caused by in-memory database differences.
- Testing all three content types (JSON, XML, YAML) ensures content negotiation works end-to-end.
- The `AbstractIntegrationTest` base class handles container lifecycle cleanly for all test classes.

**Key files:**
- [`AbstractIntegrationTest.java`](rest-with-spring-boot-and-java/src/test/java/br/com/erudio/integrationtests/testcontainers/AbstractIntegrationTest.java) — Testcontainers setup
- [`integrationtests/controllers/`](rest-with-spring-boot-and-java/src/test/java/br/com/erudio/integrationtests/controllers/) — full controller integration tests (JSON, XML, YAML, CORS)
- [`unittests/services/`](rest-with-spring-boot-and-java/src/test/java/br/com/erudio/unittests/services/) — unit tests for Person and Book services

---

### 14. Containerization with Docker

**What it does:** The entire stack (API + MySQL + Portainer) runs with a single `docker-compose up` command. The API image is built from a minimal Dockerfile using the Eclipse Temurin JDK 21 base image.

**Why I built it this way:**
- Docker ensures the application runs identically across any environment — developer laptop, CI server, or cloud VM.
- **Portainer** is included in the Compose stack to provide a visual Docker management dashboard on port 9000.
- The `erudio-network` bridge network isolates the services and lets the API reach MySQL by the `db` hostname — no IP hardcoding.

**Key files:**
- [`docker-compose.yml`](docker-compose.yml)
- [`Dockerfile`](rest-with-spring-boot-and-java/Dockerfile)

---

## 📡 API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/auth/signin` | Obtain JWT access + refresh tokens | ❌ Public |
| `PUT` | `/auth/refresh/{username}` | Refresh an access token | ❌ Public |
| `POST` | `/auth/createUser` | Register a new user | ❌ Public |
| `GET` | `/api/person/v1` | Paginated list of people (JSON/XML/YAML) | ✅ JWT |
| `GET` | `/api/person/v1/{id}` | Find person by ID | ✅ JWT |
| `GET` | `/api/person/v1/findPeopleByName/{firstName}` | Search people by name | ✅ JWT |
| `GET` | `/api/person/v1/exportPage` | Export people page (XLSX/CSV/PDF) | ✅ JWT |
| `GET` | `/api/person/v1/export/{id}` | Export person PDF report with QR code | ✅ JWT |
| `POST` | `/api/person/v1` | Create a person | ✅ JWT |
| `POST` | `/api/person/v1/massCreation` | Bulk import people from XLSX/CSV | ✅ JWT |
| `PUT` | `/api/person/v1` | Update a person | ✅ JWT |
| `PATCH` | `/api/person/v1/{id}` | Disable a person | ✅ JWT |
| `DELETE` | `/api/person/v1/{id}` | Delete a person | ✅ JWT |
| `GET` | `/api/book/v1` | Paginated list of books (JSON/XML/YAML) | ✅ JWT |
| `GET` | `/api/book/v1/{id}` | Find book by ID | ✅ JWT |
| `POST` | `/api/book/v1` | Create a book | ✅ JWT |
| `PUT` | `/api/book/v1` | Update a book | ✅ JWT |
| `DELETE` | `/api/book/v1/{id}` | Delete a book | ✅ JWT |
| `POST` | `/api/file/v1/uploadFile` | Upload a single file | ✅ JWT |
| `POST` | `/api/file/v1/uploadMultipleFiles` | Upload multiple files | ✅ JWT |
| `GET` | `/api/file/v1/downloadFile/{fileName}` | Download a file | ✅ JWT |
| `POST` | `/api/email/v1` | Send a plain email | ✅ JWT |
| `POST` | `/api/email/v1/withAttachment` | Send email with attachment | ✅ JWT |

> 💡 The full interactive documentation is available at the Swagger UI root URL once the application is running.

---

## 🚀 Running Locally

### Prerequisites
- Docker & Docker Compose
- Java 21 (for local development)
- Maven 3.8+

### With Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/JoaoVitorLima/rest-with-spring-boot-and-java.git
cd rest-with-spring-boot-and-java

# Set email credentials (optional — only needed for email endpoints)
export EMAIL_USERNAME=your@gmail.com
export EMAIL_PASSWORD=your-app-password

# Start the full stack
docker-compose up -d
```

The API will be available at `http://localhost:80` and Swagger UI at `http://localhost:80`.

### Local Development

```bash
cd rest-with-spring-boot-and-java

# Run with Maven (requires MySQL running locally on port 3308)
./mvnw spring-boot:run

# Run tests
./mvnw test
```

---

## 📁 Project Structure

```
rest-with-spring-boot-and-java/
├── src/main/java/br/com/erudio/
│   ├── config/          # Security, CORS, OpenAPI, Email, ObjectMapper configs
│   ├── controllers/     # REST controllers + OpenAPI documentation interfaces
│   ├── data/dto/        # Data Transfer Objects (Person, Book, Security, Email)
│   ├── exception/       # Custom exceptions + global @ControllerAdvice handler
│   ├── file/
│   │   ├── exporter/    # Export pipeline: Factory + CSV/XLSX/PDF implementations
│   │   └── importer/    # Import pipeline: Factory + CSV/XLSX implementations
│   ├── mail/            # Fluent email builder (EmailSender)
│   ├── mapper/          # Dozer-based object mapping utilities
│   ├── model/           # JPA entities (Person, Book, User, Permission)
│   ├── repository/      # Spring Data JPA repositories with custom JPQL queries
│   ├── security/jwt/    # JWT token provider + request filter
│   ├── serialization/   # Custom YAML HTTP message converter
│   └── services/        # Business logic layer
├── src/main/resources/
│   ├── db/migration/    # 18 Flyway versioned SQL scripts
│   └── templates/       # JasperReports JRXML templates
├── src/test/java/
│   ├── integrationtests/ # REST Assured full integration tests (JSON/XML/YAML/CORS)
│   ├── repository/       # Repository-level integration tests
│   └── unittests/        # Mockito-based unit tests
├── Dockerfile            # Eclipse Temurin JDK 21 image
└── docker-compose.yml    # MySQL + API + Portainer stack
```

---

<div align="center">

**Built by [João Vitor Lima](https://github.com/JoaoVitorLima)**

*Java · Spring Boot · REST · Security · Docker · Testing*

</div>
