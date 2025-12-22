# Wallet Service API

A Spring Boot REST API for managing wallet operations with PostgreSQL database.

## Features

- Create wallets with initial balance
- Credit/debit wallet transactions
- Transfer between wallets atomically
- Idempotent operations using idempotency keys
- Store amounts in minor units (cents) to avoid floating-point errors
- PostgreSQL database with proper indexing
- Transaction support for atomic updates

## API Endpoints

### Wallet Endpoints
- `POST /wallets` - Create a new wallet
    - Request Body: WalletRequest
    - Response: WalletResponse (201 Created)

- `GET /wallets/{id}` - Get wallet details by ID
    - Path Variable: id (Long)
    - Response: WalletResponse (200 OK)

### Transaction Endpoints
- `POST /transactions` - Create a new transaction (credit/debit)
    - Request Body: TransactionRequest
    - Response: TransactionResponse (201 Created)

- `POST /transactions/transfer` - Transfer between wallets
    - Request Body: TransferRequest
    - Response: TransactionResponse (201 Created)
## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Docker (optional, for containerized setup)

## Setup Instructions

### 1. Database Setup

#### Option A: Using Docker (Recommended)
```bash
# Create and start PostgreSQL container
docker run --name wallet-postgres \
  -e POSTGRES_DB=wallet_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:15

# Or use docker-compose with provided docker-compose.yml
docker-compose up -d
```

#### Option B: Manual Setup
1. Install PostgreSQL 15+
2. Create database and user:
```sql
CREATE DATABASE wallet_db;
CREATE USER postgres WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE wallet_db TO postgres;
```

### 2. Application Setup

#### Docker Setup
```bash
# Build the application
docker build -t wallet-service .

# Run the service connected to database
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/wallet_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  wallet-service
```

#### Local Development
```bash
# Build with Maven
./mvnw clean package

# Run the application
java -jar target/WalletService-0.0.1-SNAPSHOT.jar
```

### 3. Environment Configuration
Ensure these environment variables are set in your system or .env file:
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SERVER_PORT (default: 8080)

