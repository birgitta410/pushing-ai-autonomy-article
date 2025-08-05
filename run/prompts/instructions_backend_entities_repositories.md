Generate Spring Boot Java files for Entities, Repositories, and their tests

Get your architecture context from the architecture specification file in the workspace, architecture.md

**Entity Definitions**:
Look for the entity definitions file in the workspace

**Base Package**: derive from architecture

**Instructions**:
Determine what type of Spring Boot components should be generated for the data access layer

**Code**:

Use the reference application MCP server to get samples for entity, repository, and repository code files, and follow those examples exactly as a starting point, applied to this application's domain.

- **Entity files** (e.g., Book.java, User.java)

- **Repository files** Only add the minimum amounts of methods needed for the CRUD operations currently specified.

- **Test files** for Repositories: Create tests for each Repository

**Requirements**:
- Implement complete functionality, not just stubs
- Add error handling and validation
- Follow the architecture specifications provided
- Ensure proper JPA relationships and mappings

**Definition of your success**:
- the application can startup successfully
- the tests are all green
- Repository tests verify data persistence and retrieval correctly