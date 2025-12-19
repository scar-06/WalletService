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