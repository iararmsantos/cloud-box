# Cloud Box

A Java and Spring Boot project for practicing backend engineering through a simulated cloud infrastructure management API.

## Technology Stack

- Java 27
- Spring Boot 4.1.2
- Maven
- Spring Data JPA
- MySQL
- JUnit 5
- Mockito

## Project Scope

Cloud Box provides a REST API to manage virtual machines, including:

- VM creation, retrieval, update and deletion
- VM provisioning
- VM start and stop operations
- Lifecycle state validation

## VM Lifecycle

Supported statuses:

- `PROVISIONING`
- `RUNNING`
- `STOPPED`

### Lifecycle Rules

| Current status | Operation | Expected result |
|---|---|---|
| STOPPED | Start | RUNNING |
| RUNNING | Start | RUNNING |
| RUNNING | Stop | STOPPED |
| STOPPED | Stop | STOPPED |
| STOPPED | Provision | PROVISIONING |
| PROVISIONING | Start | HTTP 409 |
| PROVISIONING | Stop | HTTP 409 |

Repeated start and stop requests are treated as successful when the VM is already in the requested state.

## API Endpoints

Document the CRUD and lifecycle endpoints here, including example request and response bodies.

## Error Handling

- `400 Bad Request`: Invalid input
- `404 Not Found`: VM does not exist
- `409 Conflict`: Requested lifecycle operation is not permitted

## Testing

The project includes unit tests for the service, entity, status and DTO mapping layers.

## Future Improvements

- Controller and repository integration tests
- Database migrations
- Pagination and filtering
- Authentication and authorization
- Concurrent lifecycle operation handling
- Asynchronous provisioning