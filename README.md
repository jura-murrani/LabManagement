# Lab Management System

A comprehensive Laboratory Management System built with Spring Boot, providing functionality for managing patients, doctors, lab technicians, analysis requests, and results.

## Features

- **User Management**: Support for multiple user roles (Admin, Doctor, Lab Technician, Patient)
- **Patient Management**: Register and manage patient information
- **Doctor Management**: Manage doctor profiles and departments
- **Lab Technician Management**: Handle lab technician accounts
- **Analysis Requests**: Patients can request lab analyses
- **Result Management**: Lab technicians can enter and manage analysis results
- **Visit Management**: Track patient visits
- **Examination Types & Categories**: Configure examination types and categories
- **Security**: Spring Security integration with role-based access control

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security**
- **Spring Data JPA**
- **Thymeleaf** (Template Engine)
- **MySQL** (Database)
- **Maven** (Build Tool)
- **Lombok** (Reducing Boilerplate)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL Database

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/LabManagement.git
cd LabManagement
```

2. Configure the database in `src/main/resources/application.properties`

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## Project Structure

```
src/
├── main/
│   ├── java/com/example/LabManagement/
│   │   ├── Configuration/     # Security and data initialization
│   │   ├── Controller/        # REST and MVC controllers
│   │   ├── Entity/            # JPA entities
│   │   ├── Repository/        # Data access layer
│   │   ├── Service/           # Business logic
│   │   ├── dto/               # Data Transfer Objects
│   │   └── Exception/         # Custom exceptions
│   └── resources/
│       ├── templates/          # Thymeleaf templates
│       └── static/             # CSS and static resources
└── test/                       # Test files
```

## License

This project is licensed under the MIT License.

