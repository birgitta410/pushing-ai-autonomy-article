Generate Spring Boot Java files for Controllers, their tests

Get your architecture context from the architecture specification file in the workspace, architecture.md

**Entity Definitions**:
Look for the entity definitions file in the workspace

**Base Package**: derive from architecture

**Instructions**:
Determine what type of Spring Boot components should be generated for the web/controller layer. The Service layer should already exist, double check that first.

**Examples**:

- **Controller files**: 

Use the reference application MCP server to get samples for controller and controller test files, and follow those examples exactly as a starting point, applied to this application's domain.

- **Test files** for Controllers: test all endpoints including error cases, verify HTTP status codes and response content

- **DTO files** (e.g., BookDto.java, CreateBookRequest.java): Simple data transfer objects with validation annotations, and tests that test the mapping of Entities to DTOs and vice versa.

**Requirements**:
- Implement complete functionality, not just stubs
- Use proper error handling and validation
- Follow the architecture specifications provided
- Use and test proper HTTP status codes for different operations
- Implement proper request/response handling with @Valid for validation
- Use constructor injection for service dependencies

**Definition of your success**:

- the application can startup successfully
- the tests are all green
- All curls in the test script are giving the expected results
- Controller tests verify proper HTTP handling and service integration