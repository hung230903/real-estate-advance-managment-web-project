# 🏢 Estate Advance - Real Estate Management System

A full-stack web application built with **Spring Boot 3** for managing real estate buildings and users. The system features a hybrid authentication architecture combining **Session-based** login for the admin dashboard and **JWT (JSON Web Token)** for RESTful API access.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Features](#-features)
- [Authentication & Authorization](#-authentication--authorization)
- [API Endpoints](#-api-endpoints)
- [Configuration](#-configuration)
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
│   ├── CustomerAPI.java           # Customer CRUD API
│   ├── ContactAPI.java            # Contact form API
│   └── UserAPI.java               # User CRUD, Login & Register API
├── components/
│   └── JwtTokenUtils.java         # JWT token generation & validation
├── config/
│   ├── ApplicationConfig.java     # General application configuration
│   ├── ModelMapperConfig.java     # ModelMapper bean configuration
│   └── WebSecurityConfig.java     # Spring Security configuration
├── constant/
│   └── SystemConstant.java        # Application constants
├── controller/                    # MVC Controllers (Thymeleaf)
│   ├── admin/                     # Admin dashboard controllers
│   │   ├── AdminController.java   # Login & Registration controllers
│   │   ├── building/              # Building management controllers
│   │   │   └── BuildingController.java
│   │   ├── customer/              # Customer management controllers
│   │   │   └── CustomerController.java
│   │   └── user/                  # User management controllers
│   │       ├── UserController.java
│   │       └── ProfileController.java
│   └── web/                       # Public pages controllers
│       └── HomeController.java
├── converter/                     # DTO <-> Entity converters
│   ├── UserConverter.java
│   ├── CustomerConverter.java
│   ├── TransactionConverter.java
│   ├── BuildingConverter.java
│   └── RentAreaConverter.java
├── entities/                      # JPA Entities
│   ├── BaseEntity.java            # Common auditing fields
│   ├── UserEntity.java
│   ├── BuildingEntity.java
│   ├── CustomerEntity.java
│   ├── TransactionEntity.java
│   └── RentAreaEntity.java
├── enums/
│   ├── CustomerStatus.java        # Customer status enum
│   ├── District.java              # District enum
│   ├── RentType.java              # Rent type enum
│   └── TransactionType.java       # Transaction type enum
├── exceptions/                    # Global Exception Handling
│   ├── GlobalExceptionHandler.java
│   └── InvalidEntityException.java
├── filters/
│   └── JwtTokenFilter.java        # JWT authentication filter
├── models/                        # Request/Response/DTOs
│   ├── dtos/                      # Data Transfer Objects
│   │   ├── AbstractDTO.java
│   │   ├── AssignmentBuildingDTO.java
│   │   ├── AssignmentCustomerDTO.java
│   │   ├── BuildingDTO.java
│   │   ├── CustomerDTO.java
│   │   ├── LoginDTO.java
│   │   ├── PasswordDTO.java
│   │   ├── ResponseDTO.java
│   │   ├── StaffResponseDTO.java
│   │   ├── TransactionDTO.java
│   │   └── UserDTO.java
│   ├── request/                   # Request Payloads
│   │   ├── BuildingSearchRequestDTO.java
│   │   └── CustomerSearchRequest.java
│   └── response/                  # Response Payloads
│       └── BuildingSearchResponseDTO.java
├── pagination/
│   └── PaginationResult.java      # Generic pagination utility
├── repositories/                  # Spring Data JPA Repositories
│   ├── AccountRepository.java
│   ├── BuildingRepository.java
│   ├── CustomerRepository.java
│   ├── TransactionRepository.java
│   ├── UserRepository.java
│   ├── custom/                    # Custom Repository Interfaces
│   │   ├── BuildingRepositoryCustom.java
│   │   ├── CustomerRepositoryCustom.java
│   │   └── UserRepositoryCustom.java
│   └── impl/                      # Custom Repository Implementations
│       ├── BuildingRepositoryImpl.java
│       ├── CustomerRepositoryImpl.java
│       └── UserRepositoryImpl.java
├── security/
│   ├── MyUser.java                # Custom UserDetails implementation
│   ├── AuditorAwareImpl.java      # Captures user for auditing
│   ├── CustomSuccessHandler.java  # Role-based redirect after login
│   └── oauth2/                    # OAuth2 integration
│       ├── DatabaseOAuth2UserService.java
│       └── DatabaseOidcUserService.java
├── services/                      # Business logic layer
│   ├── UserService.java
│   ├── BuildingService.java
│   ├── CustomerService.java
│   ├── TransactionService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── UserDetailsServiceImpl.java
│       ├── BuildingServiceImpl.java
│       ├── CustomerServiceImpl.java
│       └── TransactionServiceImpl.java
└── utils/
    ├── MessageUtils.java
    └── SecurityUtils.java

src/main/resources/
├── application.properties.example # Configuration template (copy to application.properties)
├── application.properties         # Application configuration (git-ignored)
├── static/                        # CSS, JS, images
│   ├── admin_dashboard/           # Admin panel assets (custom.css, custom.js)
│   └── login/                     # Login/Register page assets
└── templates/                     # Thymeleaf HTML templates
    ├── login.html                 # Login page
    ├── register.html              # Registration page
    ├── admin/                     # Admin dashboard pages
    │   ├── common/                # Shared fragments (menu, sidebar, header)
    │   ├── building/              # Building management pages
    │   ├── customer/              # Customer management pages
    │   └── user/                  # User management pages
    └── web/                       # Public-facing pages
        ├── contact.html
        └── index.html
```

---

## ✨ Features

### 🏗 Building Management

- Search buildings with multiple filters (district, type, area, price...).
- Create, update, and delete buildings.
- Assign/unassign staff to buildings (Manager only).

### 👥 Customer Management

- List, search, create, update, and soft-delete customers.
- **Transaction History**: Track detailed interaction logs for each customer:
  - **CSKH (Customer Care)**: Log communication and feedback.
  - **DDX (Site Visits)**: Track building viewing history.

### 👥 User Management (Manager only)

- List, create, update, and soft-delete users.
- User profile viewing and editing with Avatar upload support.
- Password management and reset functionality.

### 🔐 Authentication & Registration

- **Web Login**: Session-based login with role-based redirect.
- **OAuth2 Login**: Integrated support for Google and Facebook login.
- **API Login**: JWT token-based authentication.
- **User Registration**: Public registration with automatic `ROLE_USER` assignment.

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

1. permitAll → /login, /register, Public web pages, assets
2. STAFF + MANAGER → /admin/users/userImage (Avatar access)
3. MANAGER only → /admin/users/**, User APIs, Assignments, and ALL Deletions
4. STAFF + MANAGER → /admin/**, /admin/api/** (Core CRUD operations)
5. authenticated() → Any other requests (Security fallback)

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

| Method   | Endpoint                          | Auth            | Description              |
| -------- | --------------------------------- | --------------- | ------------------------ |
| `GET`    | `/admin/api/buildings`            | STAFF / MANAGER | Search buildings         |
| `POST`   | `/admin/api/buildings`            | STAFF / MANAGER | Create building          |
| `PUT`    | `/admin/api/buildings`            | STAFF / MANAGER | Update building          |
| `DELETE` | `/admin/api/buildings/{ids}`      | MANAGER         | Delete buildings         |
| `GET`    | `/admin/api/buildings/{id}/staff` | MANAGER         | Get assigned staff       |
| `PUT`    | `/admin/api/buildings/assign`     | MANAGER         | Assign staff to building |

#### Customer & Transaction Management

| Method   | Endpoint                          | Auth            | Description                     |
| -------- |-----------------------------------| --------------- |---------------------------------|
| `GET`    | `/admin/api/customers`            | STAFF / MANAGER | Search customers                |
| `POST`   | `/admin/api/customers`            | STAFF / MANAGER | Create/Update customer          |
| `DELETE` | `/admin/api/customers/{ids}`      | MANAGER         | Delete customers by ID list     |
| `GET`    | `/admin/api/customers/{id}/staff` | MANAGER         | Get assigned staff              |
| `PUT`    | `/admin/api/customers/assign`     | MANAGER         | Assign staff to customer        |
| `POST`   | `/admin/api/transactions`               | STAFF / MANAGER | Save/Update interaction log     |
| `DELETE` | `/admin/api/transactions/{id}`          | MANAGER         | Soft Delete transaction by id   |
| `GET`    | `/admin/api/transactions/customer/{id}` | STAFF / MANAGER | Get transaction history         |

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

## 🗄 Configuration

The application uses **MySQL** and **Google + Facebook** credentials for OAuth2. Configure everything in `application.properties`:

```properties
#Database
spring.datasource.url=jdbc:mysql://localhost:3306/estateadvance?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
# OAuth2 Google
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=profile,email
# OAuth2 Facebook
spring.security.oauth2.client.registration.facebook.client-id=YOUR_FACEBOOK_CLIENT_ID
spring.security.oauth2.client.registration.facebook.client-secret=YOUR_FACEBOOK_CLIENT_SECRET
spring.security.oauth2.client.registration.facebook.scope=email,public_profile

```


### Key Settings

| Property                              | Value          | Description                     |
| ------------------------------------- | -------------- | ------------------------------- |
| `server.port`                         | `9999`         | Application port                |
| `api.prefix`                          | `/admin/api`   | Base path for REST APIs         |
| `jwt.expiration`                      | `2592000`      | Token TTL in seconds (30 days)  |
| `jwt.secretKey`                       | Base64-encoded | Signing key for JWT             |
| `spring.jpa.hibernate.ddl-auto`       | `none`         | No auto DDL generation          |
| `spring.security.oauth2.*.google.*`   | Configs        | Google OAuth2 Client Settings   |
| `spring.security.oauth2.*.facebook.*` | Configs        | Facebook OAuth2 Client Settings |

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
   - `spring.security.oauth2.client.registration.google.*` — your Google OAuth2 credentials
   - `spring.security.oauth2.client.registration.facebook.*` — your Facebook OAuth2 credentials

   > ⚠️ **Never commit `application.properties` to version control.** It contains database passwords, JWT secret keys, and OAuth2 credentials.

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
4. Login with **MANAGER** or **STAFF** credentials to access the admin dashboard.

### REST API (Postman)

1. Send a `POST` request to `/admin/api/users/login` with credentials in JSON body.
2. Copy the JWT token from the response `data` field.
3. For subsequent requests, add the token in the **Authorization** header: `Bearer <token>`.

---

## 👤 User Roles

| Role        | Code            | Permissions                                                   |
|-------------| --------------- | ------------------------------------------------------------- |
| **Manager** | `ROLE_MANAGER`  | Full access: User, Building (Assign), Customer & Transactions |
| **Staff**   | `ROLE_STAFF`    | Building, Customer & Transaction management, Profile access   |
| **User**    | `ROLE_USER`     | Public page access, default role for self-registration        |

### Role Hierarchy

```
MANAGER > STAFF > USER
```

- **MANAGER** can do everything a STAFF can, plus manage users and assign staff.
- **STAFF** can access the admin dashboard and manage buildings/customers.
- **USER** can only access public pages. This is the default role for self-registered accounts.

---

## 📄 License

This project is for educational purposes.
