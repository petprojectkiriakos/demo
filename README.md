# Demo Application

A Spring Boot REST API application for managing automated trading actions with PostgreSQL database integration.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Code Style](#code-style)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Docker Compose Deployment](#docker-compose-deployment)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Features](#features)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [Additional Links](#additional-links)

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- **Java 21** (OpenJDK or Oracle JDK)
- **Docker** and **Docker Compose** (for containerized deployment)
- **Git** (for version control)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd demo
```

### 2. Make Gradle Wrapper Executable

```bash
chmod +x gradlew
```

### 3. Quick Start with Docker Compose

The fastest way to get the application running with all dependencies:

```bash
cd local/deploy
docker-compose up --build
```

This will start both the PostgreSQL database and the Spring Boot application.

## 🎨 Code Style

This project uses [Spotless](https://github.com/diffplug/spotless) with Google Java Format for consistent code formatting.

### Check Code Formatting

```bash
./gradlew spotlessCheck
```

### Apply Code Formatting

```bash
./gradlew spotlessApply
```

### Formatting Rules

- **Google Java Format** style
- Automatic formatting on build
- CI/CD pipeline enforces formatting checks

## 🔨 Building the Project

### Build the Application

```bash
./gradlew build
```

### Clean Build

```bash
./gradlew clean build
```

### Build without Tests

```bash
./gradlew build -x test
```

### Build Docker Image

```bash
docker build -t demo-app .
```

## ▶️ Running the Application

### Local Development (with external PostgreSQL)

1. Ensure PostgreSQL is running on `localhost:5432`
2. Update database configuration in `src/main/resources/application.properties`
3. Run the application:

```bash
./gradlew bootRun
```

### Running with Docker

```bash
# Build the JAR file
./gradlew build

# Run with Docker
docker run -p 8080:8080 demo-app
```

### Application URLs

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Documentation**: http://localhost:8080/v3/api-docs

## 🐳 Docker Compose Deployment

For a complete local development environment with database:

### 1. Navigate to deployment directory

```bash
cd local/deploy
```

### 2. Start all services

```bash
# Start in background
docker-compose up -d

# Start with logs
docker-compose up

# Build and start
docker-compose up --build
```

### 3. Stop services

```bash
docker-compose down
```

### 4. Reset database

```bash
docker-compose down -v
docker-compose up --build
```

### Services Included

- **PostgreSQL Database** (port 5432)
- **Demo Application** (port 8080)
- **Persistent data volume** for database

### Environment Configuration

The Docker Compose setup includes:

- Database: `yourdb`
- Username: `youruser` 
- Password: `yourpass`
- Database initialization via `init.sql`

## 📚 API Documentation

### OpenAPI Specification

The application includes comprehensive API documentation using OpenAPI 3.0:

- **Specification**: `src/main/resources/openapi.yml`
- **Interactive UI**: http://localhost:8080/swagger-ui.html
- **JSON Format**: http://localhost:8080/v3/api-docs

### Available Endpoints

#### User Management
- `POST /user/login` - User authentication/registration

#### Action Management
- `POST /actions` - Create new trading actions

### Example API Calls

```bash
# User login
curl -X POST "http://localhost:8080/user/login?name=John&email=john@example.com"

# Create buy action
curl -X POST "http://localhost:8080/actions" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "description": "Buy 10 shares of XYZ",
    "type": "BuyAutomaticAction",
    "targetPrice": 100.5,
    "divergenceTolerance": 0.5,
    "priceIsLessThanTarget": true,
    "contextId": "CTX-123"
  }'
```

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test

```bash
./gradlew test --tests "DemoApplicationTests"
```

### Test with Coverage

```bash
./gradlew test jacocoTestReport
```

### Integration Tests

The application includes integration tests that verify:
- Application context loading
- Database connectivity
- REST endpoint functionality

## ✨ Features

### Current Features

- **User Management**: Simple user registration and authentication
- **Trading Actions**: Support for buy/sell automatic actions
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **API Documentation**: OpenAPI/Swagger integration
- **Containerization**: Docker support with multi-stage builds
- **Code Quality**: Automated formatting and CI/CD checks

### Planned Features

*This section will be updated as new features are planned and implemented.*

- [x] **Kafka Service Cycle**: Comprehensive message processing system for action management (see [KAFKA_SERVICE_CYCLE.md](KAFKA_SERVICE_CYCLE.md))
- [ ] Advanced user authentication and authorization
- [ ] Real-time price monitoring integration
- [ ] Action execution engine
- [ ] Portfolio management
- [ ] Advanced reporting and analytics
- [ ] WebSocket support for real-time updates
- [ ] Additional trading action types

## 📖 Documentation

### Architecture Overview

This application follows a layered architecture:

```
src/main/java/com/example/evooq/demo/
├── domain/          # Domain entities and business logic
├── kafka/           # Kafka message models and consumers  
├── resource/        # REST controllers and DTOs
│   ├── controller/  # REST endpoints
│   ├── model/       # Resource DTOs
│   └── mapper/      # Entity-DTO mappings
├── services/        # Business services and cycle orchestration
└── DemoApplication  # Main Spring Boot application
```

### Database Schema

- **Users**: User information and authentication
- **Actions**: Trading action definitions and configurations

### Configuration

- **Application Properties**: `src/main/resources/application.properties`
- **OpenAPI Specification**: `src/main/resources/openapi.yml`
- **Database Configuration**: Via environment variables in Docker Compose

### Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Database**: PostgreSQL 16
- **ORM**: JPA/Hibernate
- **Documentation**: OpenAPI 3.0 + Swagger UI
- **Mapping**: MapStruct 1.5.5
- **Build Tool**: Gradle 8.14.3
- **Containerization**: Docker + Docker Compose

## 🤝 Contributing

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Format** code: `./gradlew spotlessApply`
4. **Test** changes: `./gradlew test`
5. **Commit** changes: `git commit -m 'Add amazing feature'`
6. **Push** branch: `git push origin feature/amazing-feature`
7. **Create** a Pull Request

### Code Standards

- Follow **Google Java Format** style (enforced by Spotless)
- Write **unit tests** for new functionality
- Update **API documentation** for endpoint changes
- Ensure **CI/CD checks** pass before submitting PR

### Local Development Setup

```bash
# Install dependencies and build
./gradlew build

# Start development environment
cd local/deploy
docker-compose up -d db  # Database only
cd ../..
./gradlew bootRun       # Application in development mode
```

## 🔗 Additional Links

### Documentation & Guides

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Reference](https://spring.io/projects/spring-data-jpa)
- [OpenAPI Specification](https://spec.openapis.org/oas/v3.0.3)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

### Tools & Libraries

- [Gradle Build Tool](https://gradle.org/)
- [Spotless Code Formatter](https://github.com/diffplug/spotless)
- [MapStruct Documentation](https://mapstruct.org/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Development Resources

- [Spring Boot Starters](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using.build-systems.starters)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

**Made with ❤️ using Spring Boot and Java 21**