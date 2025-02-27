# SWIFT Code Management System

## Overview
A Spring Boot application for managing SWIFT codes and bank information. This system provides REST APIs for handling SWIFT codes, country data, and bank details with support for Excel file uploads.

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── pl/dawid/poradzinski/remitly/swift/swift/
│   │       ├── controller/        # REST endpoints
│   │       ├── dto/              # Data Transfer Objects
│   │       ├── exception/         # Custom exceptions
│   │       ├── mapper/           # Entity-DTO mappers  
│   │       ├── repository/       # JPA repositories
│   │       ├── service/          # Business logic
│   │       └── sql/              # Entity classes
│   └── resources/
│       └── application.properties # Configuration
└── test/
    └── java/                     # Test classes
```

## Excel Upload Format Specification


### File Requirements
- Format: `.xlsx`, `.xls`, or `.xlsm`
- MIME types supported:
  - `application/vnd.ms-excel`
  - `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
  - `application/vnd.ms-excel.sheet.macroEnabled.12`

### Excel Structure
 - Header Row
   - `The first row should be a header row and will be skipped during processing`
- Data Rows
   - `Each subsequent row should contain the following columns in order:`
```
| Column | Index | Description | Example |
|--------|-------|-------------|---------|
| Country ISO2 | 0 | Two-letter ISO country code | "PL" |
| SWIFT Code | 1 | Bank SWIFT/BIC code (XXX suffix indicates headquarters) | "BREXPLPWXXX" |
| Bank Name | 3 | Full name of the bank | "Bank Example SA" |
| Address | 4 | Bank branch address | "ul. Bankowa 1, 00-000 Warsaw" |
| Country Name | 6 | Full country name | "POLAND" |
```

### Special Rules
- SWIFT codes ending with "XXX" are automatically marked as headquarters
- Columns 2 and 5 are ignored during processing
- All values should be provided as text strings

### Example Row
```
PL | BREXPLPWXXX | - | Bank Example SA | ul. Bankowa 1, 00-000 Warsaw | - | POLAND
```

## API Endpoints

### SWIFT Codes API (`/v1/swift-codes`)

#### GET Endpoints
- `GET /country/{countryISO2Code}` - Get all SWIFT codes for a specific country
- `GET /{swiftCode}` - Get details for a specific SWIFT code

#### POST Endpoints
- `POST /upload` - Upload SWIFT codes from Excel file
  - Request: `multipart/form-data` with file parameter
  - Response: Success/error message with count of processed records

- `POST /` - Add new SWIFT code
  - Request: SwiftCodeDTO JSON
  - Response: Success message with created SWIFT code ID

#### DELETE Endpoints
- `DELETE /{swiftCode}` - Delete specific SWIFT code

## Data Models

### SwiftCodeDTO Upload
```java
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
```java
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
```java
{
    "countryISO2": "string",
    "countryName": "string",
    "swiftCodes": [SwiftCodeDTO] //nullable
}
```

## Technologies
- Java 21
- Spring Boot 3.4.2
- MySQL
- Maven
- Docker

## Building and Running

### Local Development Setup
1. Install **JDK 21** (required for local building and running)
2. Configure database connection in `src/main/resources/application.properties`
3. Run database locally with your settings
4. Build and run the application:

```bash
./mvnw spring-boot:run
```

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

The application will be available at `http://localhost:8080`

## Testing
1. Install **JDK 21** (required for local building and running)
2. Run tests with:
```bash
./mvnw test
```