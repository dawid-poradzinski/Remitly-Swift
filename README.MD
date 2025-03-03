# SWIFT Code Management System

An application for managing SWIFT codes and bank information. This system provides REST APIs for handling SWIFT codes, country data, and bank details with support for Excel file uploads.

The application was first built in Java (my main language) and then rewritten in Go, which I learned along the process.

## Features

- Upload SWIFT Codes from Excel: Allows users to upload SWIFT codes in bulk.
- Manage SWIFT Codes: Provides functionalities to update, delete, and view SWIFT codes.
- Validate SWIFT Codes: Ensures the validity of SWIFT codes in database.

## Technologies Used
The application is built two times using the following programming languages and frameworks:
### Java 21
- Spring Boot 3.4.2
- MySQL
- Maven
- Docker

### Go
- Go 1.21
- MySQL
- Docker

## Where to start

### Each language is on different branch:

- [Java](https://github.com/dawid-poradzinski/Remitly-Swift/tree/java)
    - Excel upload
    - POST upload with validation
    - GET by SWIFT code
    - GET by country
    - DELETE by SWIFT code
    - Creating references between swift codes
- [Go](https://github.com/dawid-poradzinski/Remitly-Swift/tree/golang)
    - POST upload
    - GET by SWIFT code
    - GET by country
    - DELETE by SWIFT code
    - Creating references between swift codes
    - Excel upload (Planned Feature)
    - POST validation (Planned Feature)