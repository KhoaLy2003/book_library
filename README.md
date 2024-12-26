# Book Library - Application for Managing Books, Borrowing, and Members

## Table of Contents
- [Table of Contents](#table-of-contents)
- [Description](#description)
- [Technology](#technology)
- [Feature](#feature)
- [Installation](#installation)

## Description
This is a book library management application designed to help users efficiently manage books, borrowing records, and library members. The system ensures secure access, robust database management, and a user-friendly interface for librarians and administrators.

## Technology
- Java 17
- Spring Boot 3.3
- MySQL
- Docker
- Angular
- Keycloak

## Feature
1. Book Management
- View and search books in library by ISBN or book's title
2. Borrowing Management
- Track borrowing records, including due dates and return statuses.
- Extend, cancel, and return borrowing.
3. Member Management
- Register new library members.
- Maintain member profiles, borrowing history, and contact details.

## Installation
1. Get the latest source code
2. Start requuired services using Docker
```bash
docker-compose up -d
```
3. Open terminal, go to book_library_be folder and build application
```bash
mvn clean package
```
4. Run backend application
```bash
java -jar target/book_library-1.0-SNAPSHOT.jar
```
5. Open terminal, go to book_library_fe folder and install dependencies
```bash
npm install
```
6. Run frontend application
```bash
ng serve
```
7. Usage: 
- To import data into the database from a CSV file, send a POST request to the /import endpoint on the backend.
```bash
http://localhost:8080/import
```
8. Access the application
- Open the frontend application in your browser at
```bash
http://localhost:4200
```
- Login in system
```bash
username: admin@gmail.com
password: 12345
```
