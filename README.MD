# SWIFT Code Management System

## Overview
A Spring Boot application for managing SWIFT codes and bank information. This system provides REST APIs for handling SWIFT codes, country data, and bank details with support for Excel file uploads.

## Project Structure
```
swift/
├── main.go                 # Application entry point
├── handlers/              
│   ├── swift_code_handler.go    # HTTP handlers for swift operations
│   └── swift_code_handler_test.go
└── models/
    ├── dto/               # Data Transfer Objects
    │   ├── mapper.go      # DTO mapping functions
    │   └── swift_dto.go   # DTO structures
    └── sql/               # Database models
        ├── bank.go
        ├── country.go
        └── swift_code.go

```

## Excel Upload Format Specification

## API Endpoints

### SWIFT Codes API (`/v1/swift-codes`)

#### GET Endpoints
- `GET /country/{countryISO2Code}` - Get all SWIFT codes for a specific country
- `GET /{swiftCode}` - Get details for a specific SWIFT code

#### POST Endpoints
- `POST` - Add new SWIFT code
  - Request: SwiftCodeDTO JSON

#### DELETE Endpoints
- `DELETE /{swiftCode}` - Delete specific SWIFT code

## Data Models

### SwiftCodeDTO Upload
```go
{
    "address": "string",
    "bankName": "string", 
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": boolean,
    "swiftCode": "string",
}
```

### SwiftCodeDTO Response
```go
{
    "address": "string",
    "bankName": "string", 
    "countryISO2": "string",
    "countryName": "string", //nullable
    "isHeadquarter": boolean,
    "swiftCode": "string",
    "branches": [SwiftCodeDTO] //nullable
}
```

### CountryDTO Response
```go
{
    "countryISO2": "string",
    "countryName": "string",
    "swiftCodes": [SwiftCodeDTO] //nullable
}
```

## Technologies
- Go 1.21
- MySQL
- Docker

## Building and Running

### Local Development Setup
1. Install **Go 1.21** (required for local building and running)
2. Configure database connection in `.env`
3. Run database locally with your settings
4. Build the application:
```bash
go mod download
```
4. Run the application:
```bash
go run main.go
```

## OR

### Docker Deployment
1. Install Docker
2. Build and run with Docker Compose:

```bash
docker-compose up --build
```
3. On first run application don't have access to database. Check running containers:

```bash
docker ps
```

4. Get inside mysql:
```bash
sudo docker exec -it <container_name> mysql -u root -p
```

5. Insert password. Default:
```bash
rootpassword
```

6. Create Database:
```bash
CREATE DATABASE remitlyGO;
```

7. To exit mysql:
```bash
exit
```

8. To stop the containers Ctrl+C or:
```bash
docker-compose down
```

9. You can run application with command:
```bash
docker-compose up
```

The application will be available at `http://localhost:8080`

## Testing
1. Install **Go 1.21** (required for local building and running)
2. Run tests with:
```bash
go test ./...
```