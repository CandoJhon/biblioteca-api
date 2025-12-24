# 📚 Library Management API

REST API for library book loan management system built with Spring Boot and PostgreSQL.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Features

- > Book Management: Complete CRUD operations for books
- > User Management: User registration and profile management
- > Loan System: Track book loans with status (active, returned, overdue)
- > Availability Control: Automatic book availability management
- > Search Functionality: Search by title, author, or ISBN
- > Data Validation: Input validation with error handling
- > Relational Database: PostgreSQL with properly designed schema

## Tech Stack

- > Backend: Spring Boot 3.2.1
- > Language: Java 17
- > Database: PostgreSQL 15
- > Build Tool: Maven
- > ORM: Spring Data JPA / Hibernate
- > Utilities: Lombok
- > Validation: Jakarta Bean Validation

## Prerequisites

- Java 17 or higher
- PostgreSQL 15
- Maven 3.x

## Installation

### 1. Clone the repository
```bash
git clone https://github.com/CandoJhon/biblioteca-api.git
cd biblioteca-api
```

### 2. Configure PostgreSQL
```bash
# Create database
sudo -u postgres psql
CREATE DATABASE biblioteca;
ALTER USER postgres PASSWORD 'postgres123';
\q
```

### 3. Configure application

Edit `src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca
spring.datasource.username=postgres
spring.datasource.password=postgres123
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

API will be available at: `http://localhost:8080`

## API Endpoints ##

### Health Check
```bash
GET /api/health
```

### Books

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | List all books |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/available` | List available books |
| GET | `/api/books/search?title=...` | Search by title |
| GET | `/api/books/search?author=...` | Search by author |
| POST | `/api/books` | Create new book |
| PUT | `/api/books/{id}` | Update book |
| DELETE | `/api/books/{id}` | Delete book |

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/active` | List active users |
| GET | `/api/users/search?name=...` | Search by name |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Loans

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/loans` | List all loans |
| GET | `/api/loans/{id}` | Get loan by ID |
| GET | `/api/loans/active` | List active loans |
| GET | `/api/loans/overdue` | List overdue loans |
| GET | `/api/loans/user/{userId}` | Get loans by user |
| GET | `/api/loans/book/{bookId}` | Get loans by book |
| POST | `/api/loans` | Create new loan |
| POST | `/api/loans/{id}/return` | Return book |

## Example Requests

### Create a Book
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "978-0132350884",
    "PublicationYear": 2008,
    "editorial": "New Editorial",
    "available": true
  }'
```

### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "555-1234",
    "active": true
  }'
```

### Create a Loan
```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "book": {"id": 1},
    "user": {"id": 1},
    "ExpectedReturnDate": "2025-01-15"
  }'
```

## 🗄️ Database Schema
```
books
├── id (PK)
├── title
├── author
├── isbn (unique)
├── publication_year
├── publisher
├── available
└── registration_date

users
├── id (PK)
├── first_name
├── last_name
├── email (unique)
├── phone
├── registration_date
└── active

loans
├── id (PK)
├── book_id (FK → books)
├── user_id (FK → users)
├── loan_date
├── expected_return_date
├── actual_return_date
└── status (ACTIVE, RETURNED, OVERDUE)
```

## Project Structure
```
src/main/java/com/biblioteca/api/
├── controller/       # REST Controllers
├── service/          # Business Logic
├── repository/       # Data Access Layer
├── model/            # Entity Classes
└── BibliotecaApiApplication.java
```

## Testing
```bash
./mvnw test
```

## Docker (Coming Soon)
```bash
docker-compose up
```

## 🔄 Future Improvements

- [ ] Authentication & Authorization (Spring Security)
- [ ] API Documentation (Swagger/OpenAPI)
- [ ] Pagination for list endpoints
- [ ] Advanced search filters
- [ ] Email notifications for overdue loans
- [ ] Book reservations
- [ ] Fine calculation for late returns

## Author ##

**Jhon Cando**

- GitHub: [@CandoJhon](https://github.com/CandoJhon)
- LinkedIn: [jhon-cando](https://linkedin.com/in/jhon-cando)

## License

This project is licensed under the MIT License.

## Contributing

Contributions, issues, and feature requests are welcome!

