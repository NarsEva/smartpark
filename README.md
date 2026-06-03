# SmartPark

SmartPark is a Spring Boot parking management application for registering vehicles, creating parking lots, checking vehicles in and out, and viewing current parking occupancy.

## Project Overview

The application manages parking lots and parking records with a simple REST API. It supports:

- Creating parking lots
- Registering vehicles
- Checking vehicles in and out of a parking lot
- Viewing occupancy and currently parked vehicles

Timestamps for parking events are returned in a readable format using the timezone supplied by the client through the `Time-Zone` header. If the header is omitted, the server default timezone is used.

## Features

- Create and retrieve parking lot records
- Register vehicles with unique license plates
- Check vehicles into a lot
- Check vehicles out of a lot
- View parking lot availability
- View parked vehicles in a specific lot
- Bean validation for required fields and field length rules
- H2 in-memory database for local development and testing

## Tech Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- Lombok
- H2 Database
- JUnit 5
- Mockito

## Project Structure

```text
src/main/java/com/nariette/smartpark
  controller
  dto
    request
    response
  entity
  exception
  mapper
  repository
  service
src/main/resources
src/test/java/com/nariette/smartpark
```

## Prerequisites

- Java 17
- Maven Wrapper or Maven
- Git

## Build Instructions

```powershell
.\mvnw.cmd clean test
```

## Run Instructions

```powershell
.\mvnw.cmd spring-boot:run
```

The application starts on the default Spring Boot port:

- `http://localhost:8080`

## H2 Console Access

When the application is running:

- H2 Console URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:smartparkdb`
- Username: `sa`
- Password: empty

## API Endpoints

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for the full endpoint reference, request/response samples, and field details.

## Curl Appendix

Base URL:

```text
http://localhost:8080
```

Create a parking lot:

```bash
curl -X POST "http://localhost:8080/api/parking-lots" ^
  -H "Content-Type: application/json" ^
  -d "{ \"lotId\": \"LOT-001\", \"location\": \"Basement A\", \"capacity\": 10 }"
```

Create a vehicle:

```bash
curl -X POST "http://localhost:8080/api/vehicles" ^
  -H "Content-Type: application/json" ^
  -d "{ \"licensePlate\": \"ABC-123\", \"type\": \"CAR\", \"ownerName\": \"Jane Doe\" }"
```

Check in a vehicle:

```bash
curl -X POST "http://localhost:8080/api/parking-lots/LOT-001/check-in" ^
  -H "Content-Type: application/json" ^
  -H "Time-Zone: Asia/Manila" ^
  -d "{ \"licensePlate\": \"ABC-123\" }"
```

Check out a vehicle:

```bash
curl -X POST "http://localhost:8080/api/parking-lots/LOT-001/check-out" ^
  -H "Content-Type: application/json" ^
  -H "Time-Zone: Asia/Manila" ^
  -d "{ \"licensePlate\": \"ABC-123\" }"
```

Get lot availability:

```bash
curl "http://localhost:8080/api/parking-lots/LOT-001/availability"
```

Get parked vehicles:

```bash
curl "http://localhost:8080/api/parking-lots/LOT-001/vehicles"
```

## Running Tests

```powershell
.\mvnw.cmd clean test
```

Expected result:

- `BUILD SUCCESS`
- `Tests run > 0`
- `Failures = 0`
- `Errors = 0`

## Assumptions

- Vehicle license plate is unique.
- Lot ID is unique.
- A vehicle may only have one active parking session.
- Occupied spaces are maintained by the application.
- Authentication is intentionally omitted as per assessment requirements.
- The client may send a `Time-Zone` header, for example `Asia/Manila`, to format parking timestamps in the user’s local timezone.
