Generate Spring Boot Java files for Services and their tests

Get your architecture context from the architecture specification file in the workspace, architecture.md

**Entity Definitions**:
Look for the entity definitions file in the workspace

**Base Package**: derive from architecture

**Instructions**:
Determine what type of Spring Boot components should be generated for the service layer. Entities and repositories should already exist, double check that first.

**Examples**:

Use the reference application MCP server to get samples for service and service test code files, and follow those examples exactly as a starting point, applied to this application's domain.

- **Service files** (e.g., BookService.java): implement CRUD operations, add @Transactional

- **Test files** for Services: Create tests for each Service. Test all service methods including error cases

**Requirements**:
- Implement complete functionality, not just stubs
- Add error handling and validation
- Follow the architecture specifications provided
- Use @Transactional appropriately (readOnly for queries)
- Inject dependencies through constructor injection
- Handle business logic and data transformation between entities and DTOs. Introduce mapper classes.
- Throw appropriate domain-specific exceptions for error cases

**Definition of your success**:

- the application can startup successfully
- the tests are all green
- Service tests verify business logic and proper repository interactions