# 🏦 JBank Core API

> **Enterprise-Grade Modular Monolith Fintech Backend** built with Java 21, Spring Boot 3.4, and Clean Architecture principles.

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3.4](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)

---

## 🏗️ Architecture & Design
This project adopts a **Modular Monolith** architecture, grouping code by **Domain Feature** (`modulos/*`) rather than technical layers. This approach ensures high cohesion and low coupling, paving the way for easier microservices extraction in the future if needed.

### Key Technical Decisions
*   **Java 21**: Leveraging Virtual Threads and modern syntax.
*   **Security First**: Full implementation of **JWT (Stateless)** authentication + BCrypt.
*   **Concurrency Control**: Using `PESSIMISTIC_WRITE` locking on Wallets to prevent Race Conditions during transfers.
*   **Clean Code**: Explicit coding standards (English code / PT-BR Domain), extensive use of DTOs, and robust error handling.
*   **Observability**: Structured Logging (`@Slf4j`) in all critical services.

---

## 📦 Modules

### 1. Auth & Users (`/modulos/auth`, `/modulos/usuarios`)
*   **Features**: Signup, Login, JWT Generation, User Profile.
*   **Security**: Password encryption, Role-based access (MVP defaults to `ROLE_USER`).

### 2. Transactions (`/modulos/transacoes`)
*   **Features**: Peer-to-Peer (P2P) transfers between wallets.
*   **Consistency**: ACID transactions with **Deadlock Prevention** (Resource Ordering by ID).
*   **Precision**: Strict use of `BigDecimal` for monetary values.

### 3. Pix (`/modulos/pix`)
*   **Features**:
    *   **Keys**: Register unique keys (CPF, EMAIL, PHONE, RANDOM).
    *   **Management**: List and Delete User Keys.
    *   **Validation**: Rule enforcement (Max 5 keys/user).

---

## 🚀 Getting Started

### Prerequisites
*   **Java 21 JDK** (Script auto-detects or helps you find it)
*   **Maven** (Wrapper included)
*   **Docker** (Optional, for integration tests)

### One-Click Run (PowerShell)
We use a developer experience script to automate the build and run process.

```powershell
./start-dev.ps1
```
*Checks for Java 21 -> Builds (Skipping Tests for Speed) -> Starts App -> Opens Swagger UI*

### Manual Run
```bash
mvn clean install
java -jar target/jbank-core-0.0.1-SNAPSHOT.jar
```

---

## 📚 API Documentation
Once running, access the **Swagger UI** to explore endpoints:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Key Endpoints
*   `POST /auth/signup` - Register new user
*   `POST /auth/login` - Get JWT Token
*   `POST /transactions/transfer` - Send money
*   `POST /pix/keys` - Register Pix Key

---

## 🛠️ Project Structure
```
src/main/java/br/com/pamela/jbank/
├── infra/              # Cross-cutting support (Security, Config)
├── modulos/            # Domain Modules (The Core)
│   ├── auth/           # Login/Signup/Tokens
│   ├── carteiras/      # Wallets & Balance Logic
│   ├── pix/            # Pix Keys & Payments
│   ├── transacoes/     # Money Transfer Logic
│   └── usuarios/       # User Management
└── JBankApplication.java
```

---

*Verified by "The Exterminator" QA Code Review.*
