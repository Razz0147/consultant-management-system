# Consultant Management System (CMS)

A full-featured **Java Spring Boot** web application designed to manage consultant profiles, track availability, analyze tech stack metrics, and generate reports. Built using modern design principles, layered architecture, server-side validation, Thymeleaf, Bootstrap 5 UI, and Spring Data JPA.

---

## Table of Contents
1. [Key Features](#key-features)
2. [Tech Stack](#tech-stack)
3. [Architecture Overview](#architecture-overview)
4. [Getting Started & Setup](#getting-started--setup)
   - [Option A: Instant Launch with H2 (Default)](#option-a-instant-launch-with-h2-default)
   - [Option B: Run with MySQL Database](#option-b-run-with-mysql-database)
   - [Option C: Run with Docker Compose](#option-c-run-with-docker-compose)
5. [REST API Reference](#rest-api-reference)
6. [Report Exports (Excel & PDF)](#report-exports-excel--pdf)
7. [Running Tests](#running-tests)
8. [Submission Deliverables Checklist](#submission-deliverables-checklist)

---

## Key Features

- 📊 **Interactive Dashboard**: KPI stat cards for total consultants, active, inactive, tech count, and interactive tech distribution charts (powered by Chart.js).
- ➕ **Consultant Management**: Create, update, view, and soft/hard delete consultant profiles with phone, email, tech stack, and experience.
- 🛡️ **Server-Side Validation**: Jakarta validation (`@NotBlank`, `@Email`, `@Pattern`, `@Min`, `@Max`) with real-time feedback error alerts.
- 🔍 **Dynamic Search & Filtering**: Instant search by consultant Name or Technology stack combined with status filters (Active / Inactive).
- 📑 **Pagination & Sorting**: Server-side pagination with custom page sizes and sortable headers for every table column.
- 📥 **One-Click Exports**: Export complete consultant directories directly to **Excel (`.xlsx`)** or **PDF (`.pdf`)**.
- 🔌 **REST API Suite**: Complete set of JSON REST endpoints under `/api/v1/consultants`.
- 🐳 **Docker Ready**: Pre-configured `Dockerfile` and `docker-compose.yml` for database containerization.

---

## Tech Stack

- **Backend**: Java 17+, Spring Boot 3.3.2, Spring Data JPA, Spring MVC, Hibernate.
- **Frontend**: Thymeleaf, Bootstrap 5.3, FontAwesome 6, Chart.js, HTML5/CSS3.
- **Database**: MySQL 8.4 / H2 In-Memory Database.
- **Tools & Libraries**: Apache POI (Excel generation), OpenPDF (PDF generation), Lombok, Maven.
- **Testing**: JUnit 5, Mockito, Spring Boot Starter Test (MockMvc).

---

## Architecture Overview

The application adopts a **4-Layered Architecture Pattern**:

```
[ Controller Layer ] <---> [ Service Layer ] <---> [ Repository Layer ] <---> [ Database (MySQL / H2) ]
 (Spring MVC / REST)    (Business Rules / DTOs)   (Spring Data JPA)        (Entity Model)
```

- `com.cms.model`: Domain entities mapped with JPA annotations (`Consultant.java`).
- `com.cms.dto`: Data Transfer Objects with Bean Validation constraints (`ConsultantDto.java`).
- `com.cms.repository`: `JpaRepository` interface handling queries and pagination (`ConsultantRepository.java`).
- `com.cms.service`: Business service interfaces and implementation handling logic, data transformations, and report rendering (`ConsultantService.java`, `ConsultantServiceImpl.java`).
- `com.cms.controller`: Web MVC Controllers and REST Controllers (`DashboardController.java`, `ConsultantController.java`, `ConsultantRestController.java`, `ExportController.java`).
- `com.cms.exception`: Global exception handler (`GlobalExceptionHandler.java`).

---

## Getting Started & Setup

### Option A: Instant Launch with H2 (Default)

No MySQL database setup is required to test out of the box!

1. Clone or download the repository.
2. Open terminal in the project directory:
   ```bash
   mvn clean spring-boot:run
   ```
3. Open your browser and navigate to:
   ```text
   http://localhost:8080
   ```
4. Access the H2 Database Console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:consultant_db`, Username: `sa`, Password: empty).

---

### Option B: Run with MySQL Database

1. Import `consultant_db.sql` into your local MySQL server:
   ```bash
   mysql -u root -p < consultant_db.sql
   ```
2. Open `src/main/resources/application.properties` and uncomment the MySQL section:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/consultant_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
3. Run the application:
   ```bash
   mvn clean spring-boot:run
   ```

---

### Option C: Run with Docker Compose

To spin up both the Spring Boot app and MySQL container simultaneously:

```bash
docker-compose up --build
```
Access the application at `http://localhost:8080`.

---

## REST API Reference

All REST endpoints return standard JSON responses:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/consultants` | Get all consultants (supports `?keyword=`) |
| `GET` | `/api/v1/consultants/page` | Get paginated consultants (`?page=0&size=10&sortBy=name`) |
| `GET` | `/api/v1/consultants/{id}` | Get single consultant profile by ID |
| `POST` | `/api/v1/consultants` | Create new consultant (JSON body) |
| `PUT` | `/api/v1/consultants/{id}` | Update existing consultant profile |
| `DELETE` | `/api/v1/consultants/{id}` | Delete consultant by ID |
| `GET` | `/api/v1/consultants/stats` | Get dashboard stats JSON |

---

## Report Exports (Excel & PDF)

- **Excel Export**: Download formatted spreadsheet at `/consultants/export/excel`.
- **PDF Export**: Download printable PDF document at `/consultants/export/pdf`.

---

## Running Tests

Execute the unit tests and integration tests with Maven:

```bash
mvn test
```

---

## Submission Deliverables Checklist

- [x] **Source Code**: Complete Java Spring Boot + Thymeleaf codebase.
- [x] **SQL Script**: Included `consultant_db.sql` in root and `schema.sql` / `data.sql` in resources.
- [x] **README**: Comprehensive guide with setup and architecture details.
- [x] **Bonus Features Implemented**: Pagination & Sorting, REST APIs, Excel/PDF Exports, Unit Tests, Docker Support.
