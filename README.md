# 🏢 Estate Advance - Real Estate Management System

A full-stack web application built with **Spring Boot 3** for managing real estate buildings and users. The system features a hybrid authentication architecture combining **Session-based** login for the admin dashboard and **JWT (JSON Web Token)** for RESTful API access.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Features](#-features)
- [Authentication & Authorization](#-authentication--authorization)
- [API Endpoints](#-api-endpoints)
- [Database Configuration](#-database-configuration)
- [Getting Started](#-getting-started)
- [Usage](#-usage)
- [User Roles](#-user-roles)

---

## 🛠 Tech Stack

| Category            | Technology                              |
| ------------------- | --------------------------------------- |
| **Backend**         | Spring Boot 3.4.12, Java 17             |
| **Security**        | Spring Security 6, JWT (jjwt 0.11.5)    |
| **ORM**             | Spring Data JPA, Hibernate              |
| **Database**        | MySQL                                   |
| **Template Engine** | Thymeleaf + Thymeleaf Security Extras   |
| **Frontend**        | Bootstrap 4, jQuery, Font Awesome       |
| **Build Tool**      | Maven                                   |
| **Others**          | Lombok, ModelMapper, Jakarta Validation |

---

## 📁 Project Structure

```
src/main/java/com/webapp/
├── Main.java                      # Application entry point
├── api/                           # REST API Controllers
│   ├── BuildingAPI.java           # Building CRUD endpoints
│   └── UserAPI.java               # User CRUD, Login & Register API
├── components/
│   └── JwtTokenUtils.java         # JWT token generation & validation
├── config/
│   └── WebSecurityConfig.java     # Spring Security configuration
├── constant/
│   └── SystemConstant.java        # Application constants
├── controller/admin/              # MVC Controllers (Thymeleaf)
│   ├── AdminController.java       # Login & Registration pages
│   ├── building/
│   │   └── BuildingController.java
│   └── user/
│       ├── UserController.java
│       └── ProfileController.java
├── converter/                     # DTO <-> Entity converters
│   ├── UserConverter.java
│   └── BuildingRequestConverter.java
├── entities/                      # JPA Entities
│   ├── UserEntity.java
│   ├── BuildingEntity.java
│   └── RentAreaEntity.java
├── enums/
│   └── UserRole.java              # ROLE_MANAGER, ROLE_EMPLOYEE, ROLE_USER
├── filters/
│   └── JwtTokenFilter.java        # JWT authentication filter
├── models/dtos/                   # Data Transfer Objects
│   ├── LoginDTO.java
│   ├── UserDTO.java
│   ├── BuildingDTO.java
│   └── ResponseDTO.java
├── pagination/
│   └── PaginationResult.java      # Generic pagination utility
├── repositories/                  # Spring Data JPA Repositories
├── security/
│   ├── MyUser.java                # Custom UserDetails implementation
│   └── CustomSuccessHandler.java  # Role-based redirect after login
├── services/                      # Business logic layer
│   ├── UserService.java
│   ├── BuildingService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── UserDetailsServiceImpl.java
│       └── BuildingServiceImpl.java
└── utils/
    └── MessageUtils.java

src/main/resources/
├── application.properties.example # Configuration template (copy to application.properties)
├── application.properties         # Application configuration (git-ignored)
├── static/                        # CSS, JS, images
│   ├── admin_dashboard/           # Admin panel assets
│   └── login/                     # Login/Register page assets
└── templates/                     # Thymeleaf HTML templates
    ├── login.html                 # Login page
    ├── register.html              # Registration page
    ├── admin/                     # Admin dashboard pages
    │   ├── common/                # Shared fragments (menu, sidebar, header)
    │   ├── building/              # Building management pages
    │   └── user/                  # User management pages
    └── web/                       # Public-facing pages
        └── index.html
```

---

## ✨ Features

### 🏗 Building Management

- Search buildings with multiple filters (district, type, area, price...)
- Create, update, and delete buildings
- Assign/unassign staff to buildings (Manager only)
- Pagination support for building lists

### 👥 User Management

- List, create, update, and soft-delete users (Manager only)
- User profile viewing and editing
- Avatar/image upload support
- Password management

### 🔐 Authentication & Registration

- **Web Login**: Session-based login with role-based redirect
- **API Login**: JWT token-based authentication
- **User Registration**: Public registration with automatic `ROLE_USER` assignment
- Confirm password validation (client-side + server-side)

---

## 🔒 Authentication & Authorization

### Hybrid Architecture

This project uses a **hybrid authentication** model:

| Channel                         | Auth Method                 | Use Case                       |
| ------------------------------- | --------------------------- | ------------------------------ |
| **Web Pages** (`/admin/**`)     | Session + JSESSIONID Cookie | Admin dashboard via browser    |
| **REST APIs** (`/admin/api/**`) | JWT Bearer Token            | External apps, Postman, Mobile |

### Security Rules (Request Matchers Order)

```
1. permitAll  → /login, /register, /admin/api/users/login, /admin/api/users/register, /assets/**, /web/**
2. EMPLOYEE + MANAGER → /admin/api/users/userImage (avatar for menu bar)
3. MANAGER only → /admin/api/users/**, /admin/api/buildings/assign, /admin/api/buildings/{id}/staff
4. EMPLOYEE + MANAGER → /admin/**
5. permitAll → everything else
```

### JWT Token Structure

When a user logs in via the API, the JWT token contains the following claims:

```json
{
  "userName": "manager1",
  "role": "ROLE_MANAGER",
  "fullName": "Manager One",
  "sub": "manager1",
  "exp": 1779346609
}
```

- Token expiration: **30 days** (configurable via `jwt.expiration`)
- Signing algorithm: **HS256**

---

## 📡 API Endpoints

### Public Endpoints (No authentication required)

| Method | Endpoint                    | Description                 |
| ------ | --------------------------- | --------------------------- |
| `POST` | `/admin/api/users/login`    | Login and receive JWT token |
| `POST` | `/admin/api/users/register` | Register a new user account |

### Protected Endpoints (JWT Bearer Token required)

#### User Management (MANAGER only)

| Method   | Endpoint                         | Description                  |
| -------- | -------------------------------- | ---------------------------- |
| `POST`   | `/admin/api/users`               | Create a new user            |
| `PUT`    | `/admin/api/users`               | Update user information      |
| `DELETE` | `/admin/api/users`               | Soft-delete users by ID list |
| `PUT`    | `/admin/api/users/password/{id}` | Reset user password          |

#### Building Management

| Method   | Endpoint                          | Auth               | Description              |
| -------- | --------------------------------- | ------------------ | ------------------------ |
| `GET`    | `/admin/api/buildings`            | EMPLOYEE / MANAGER | Search buildings         |
| `POST`   | `/admin/api/buildings`            | EMPLOYEE / MANAGER | Create building          |
| `PUT`    | `/admin/api/buildings`            | EMPLOYEE / MANAGER | Update building          |
| `DELETE` | `/admin/api/buildings/{ids}`      | EMPLOYEE / MANAGER | Delete buildings         |
| `GET`    | `/admin/api/buildings/{id}/staff` | MANAGER            | Get assigned staff       |
| `PUT`    | `/admin/api/buildings/assign`     | MANAGER            | Assign staff to building |

### Example: Login Request

```bash
POST http://localhost:9999/admin/api/users/login
Content-Type: application/json

{
    "username": "manager1",
    "password": "123456"
}
```

**Response:**

```json
{
  "data": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful",
  "errorDetails": null
}
```

### Example: Register Request

```bash
POST http://localhost:9999/admin/api/users/register
Content-Type: application/json

{
    "userName": "newuser",
    "fullName": "New User",
    "password": "123456",
    "phone": "0123456789"
}
```

### Example: Using JWT Token

```bash
DELETE http://localhost:9999/admin/api/users
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

[1, 2, 3]
```

---

## 🗄 Database Configuration

The application uses **MySQL**. Configure your database connection in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/estateadvance?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Key Settings

| Property                        | Value          | Description                    |
| ------------------------------- | -------------- | ------------------------------ |
| `server.port`                   | `9999`         | Application port               |
| `api.prefix`                    | `/admin/api`   | Base path for REST APIs        |
| `jwt.expiration`                | `2592000`      | Token TTL in seconds (30 days) |
| `jwt.secretKey`                 | Base64-encoded | Signing key for JWT            |
| `spring.jpa.hibernate.ddl-auto` | `none`         | No auto DDL generation         |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/your-username/SpringBootWebProject.git
   cd SpringBootWebProject
   ```

2. **Set up the database:**
   - Create a MySQL database named `estateadvance`
   - Import the SQL schema (if available)

3. **Configure `application.properties`:**

   The `application.properties` file is **git-ignored** because it contains sensitive credentials. You need to create it from the provided template:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

   Then edit `application.properties` and update the following values:
   - `spring.datasource.username` — your MySQL username
   - `spring.datasource.password` — your MySQL password
   - `jwt.secretKey` — your own Base64-encoded secret key (at least 32 bytes)

   > ⚠️ **Never commit `application.properties` to version control.** It contains database passwords and JWT secret keys.

4. **Run the application:**

   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application:**
   - 🌐 Homepage: `http://localhost:9999/`
   - 🔑 Login Page: `http://localhost:9999/login`
   - 📝 Register Page: `http://localhost:9999/register`
   - 🛠 Admin Dashboard: `http://localhost:9999/admin/buildings/list`

---

## 📖 Usage

### Web Interface

1. Navigate to `/login` to access the login page.
2. Click **"Sign up here"** to register a new account (assigned `ROLE_USER` by default).
3. After registration, you'll be redirected to the login page with a success message.
4. Login with **MANAGER** or **EMPLOYEE** credentials to access the admin dashboard.

### REST API (Postman)

1. Send a `POST` request to `/admin/api/users/login` with credentials in JSON body.
2. Copy the JWT token from the response `data` field.
3. For subsequent requests, add the token in the **Authorization** header: `Bearer <token>`.

---

## 👤 User Roles

| Role                 | Code            | Permissions                                                         |
| -------------------- | --------------- | ------------------------------------------------------------------- |
| **Manager**          | `ROLE_MANAGER`  | Full access: user management, building management, staff assignment |
| **Staff / Employee** | `ROLE_EMPLOYEE` | Building management (view, create, edit), view own profile          |
| **User**             | `ROLE_USER`     | Default role for new registrations, public page access only         |

### Role Hierarchy

```
MANAGER > EMPLOYEE > USER
```

- **MANAGER** can do everything an EMPLOYEE can, plus manage users and assign staff.
- **EMPLOYEE** can access the admin dashboard and manage buildings.
- **USER** can only access public pages. This is the default role for self-registered accounts.

---

## 📄 License

This project is for educational purposes.
