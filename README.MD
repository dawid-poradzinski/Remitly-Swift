# SWIFT Code Management System

## Overview
A Go application for managing SWIFT codes and bank information. This system provides REST APIs for handling SWIFT codes, country data, and bank details.

## Project Structure
```
swift/
├── main.go
├── handlers/              
│   ├── swift_code_handler.go
│   └── swift_code_handler_test.go
└── models/
    ├── dto/
    │   ├── mapper.go
    │   └── swift_dto.go
    └── sql/
        ├── bank.go
        ├── country.go
        └── swift_code.go
```

## API Endpoints

### SWIFT Codes API (`/v1/swift-codes`)

#### GET Endpoints
- `GET /country/{countryISO2Code}` - Get all SWIFT codes for a specific country
- `GET /{swiftCode}` - Get details for a specific SWIFT code

#### POST Endpoints
- `POST` - Add new SWIFT code
  - Request: SwiftCodeDTO JSON
  - Response: Success message with created SWIFT code ID

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
- Go
- MySQL
- Docker

## Building and Running

### Local Development Setup
1. Install Go

2. Configure database connection in .env

3. Run the application:

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

3. To stop the containers:
```bash
docker-compose down
```