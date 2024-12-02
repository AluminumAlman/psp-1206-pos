# Spring Boot POS Project

## Overview

This project is a Spring Boot application that integrates PostgreSQL for database operations and Swagger UI for API documentation.

## Prerequisites

Before setting up the application, ensure you have the following installed on your local machine:

- **Java 23**
- **Maven**
- **PostgreSQL** (locally or via Docker)

## Setting Up the Database Locally

### 1. Install PostgreSQL

Ensure that PostgreSQL is installed and running on your local machine. If you don't have it installed, you can download and install it from the official [PostgreSQL website](https://www.postgresql.org/download/).

### 2. Create the Database and User

Open the PostgreSQL terminal (`psql`) and execute the following SQL commands to create the database `pos` and the user `datauser` with the required privileges:

```sql
CREATE DATABASE pos;
CREATE USER datauser WITH PASSWORD '6y3wxsnq';
GRANT ALL PRIVILEGES ON DATABASE pos TO datauser;
```


## API Documentation

Once the application is running, you can interact with the API through Swagger UI:

- **API Endpoints**: [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- **Raw API Docs (OpenAPI)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)`

* * * * *
