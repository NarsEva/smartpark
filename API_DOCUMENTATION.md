# SmartPark API Documentation

Base URL:

- `http://localhost:8080`

Common notes:

- Requests and responses use JSON.
- Validation errors return HTTP `400 Bad Request`.
- Domain errors return structured JSON from `GlobalExceptionHandler`.
- Parking check-in and check-out endpoints accept an optional `Time-Zone` header.

## POST `/api/parking-lots`

Create a parking lot.

### Request

Headers:

- `Content-Type: application/json`

Body:

- `lotId` - required, max 50 characters
- `location` - required
- `capacity` - required, must be greater than 0

### Sample Request Body

```json
{
  "lotId": "LOT-001",
  "location": "Basement A",
  "capacity": 10
}
```

### Sample Response

```json
{
  "lotId": "LOT-001",
  "location": "Basement A",
  "capacity": 10,
  "occupiedSpaces": 0,
  "availableSpaces": 10
}
```

## POST `/api/vehicles`

Register a vehicle.

### Request

Headers:

- `Content-Type: application/json`

Body:

- `licensePlate` - required, max 50 characters, letters/numbers/dashes only
- `type` - required, one of `CAR`, `MOTORCYCLE`, `TRUCK`
- `ownerName` - required

### Sample Request Body

```json
{
  "licensePlate": "ABC-123",
  "type": "CAR",
  "ownerName": "Jane Doe"
}
```

### Sample Response

```json
{
  "licensePlate": "ABC-123",
  "type": "CAR",
  "ownerName": "Jane Doe"
}
```

## POST `/api/parking-lots/{lotId}/check-in`

Check a vehicle into a parking lot.

### Request

Headers:

- `Content-Type: application/json`
- `Time-Zone` - optional, e.g. `Asia/Manila`

Path parameters:

- `lotId` - parking lot ID

Body:

- `licensePlate` - required, max 50 characters, letters/numbers/dashes only

### Sample Request Body

```json
{
  "licensePlate": "ABC-123"
}
```

### Sample Response

```json
{
  "parkingRecordId": 5,
  "lotId": "LOT-001",
  "licensePlate": "ABC-123",
  "checkInTime": "Jun 4, 2026 12:47 AM +08:00"
}
```

## POST `/api/parking-lots/{lotId}/check-out`

Check a vehicle out of a parking lot.

### Request

Headers:

- `Content-Type: application/json`
- `Time-Zone` - optional, e.g. `Asia/Manila`

Path parameters:

- `lotId` - parking lot ID

Body:

- `licensePlate` - required, max 50 characters, letters/numbers/dashes only

### Sample Request Body

```json
{
  "licensePlate": "ABC-123"
}
```

### Sample Response

```json
{
  "parkingRecordId": 5,
  "lotId": "LOT-001",
  "licensePlate": "ABC-123",
  "checkInTime": "Jun 4, 2026 12:47 AM +08:00",
  "checkOutTime": "Jun 4, 2026 1:15 AM +08:00"
}
```

## GET `/api/parking-lots/{lotId}/availability`

Get a parking lot summary including occupancy.

### Request

Headers:

- None required

Path parameters:

- `lotId` - parking lot ID

### Sample Response

```json
{
  "lotId": "LOT-001",
  "location": "Basement A",
  "capacity": 10,
  "occupiedSpaces": 3,
  "availableSpaces": 7
}
```

## GET `/api/parking-lots/{lotId}/vehicles`

Get all currently parked vehicles in a lot.

### Request

Headers:

- None required

Path parameters:

- `lotId` - parking lot ID

### Sample Response

```json
[
  {
    "licensePlate": "ABC-123",
    "type": "CAR",
    "ownerName": "Jane Doe"
  }
]
```
