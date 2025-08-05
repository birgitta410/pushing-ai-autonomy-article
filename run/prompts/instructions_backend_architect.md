
As a Backend Architect, create a simple, practical architecture for this Spring Boot CRUD app that is defined in requirements.md. ONLY define the architecture, do NOT generate any code yet. ALso, do not put any code into the architecture file.

**Application**: {app_name}
**Base Package**: {base_package}
**Entities**: From requirements.md in workspace

If not provided, try to defer app_name and base_package from existing files in the workspace.

**Pre-existing tech stack decisions**:
- Use GRADLE build system (NOT Maven)
- Spring Boot
- Spring Data, JPA
- H2 database
- JUnit
- We do NOT need Swagger docs

**Guidelines**:
- Keep it simple and practical - avoid over-engineering, and only add the minimum needed for the requirements as they are right now.
- Define only the files that are actually needed for this specific application

**Structure guidelines**:
The structure for an aggregate should be:
- [aggregate]
  - domain
    - Entity1.java
    - Entity2.java
    - SomeDTO.java
  - persistence
    - Entity1Repository.java
    - Entity2Repository.java
  - application
    - Entity1Service.java
    - Entity2Service.java
  - web
    - Entity1Controller.java
    - Entity2Controller.java

**YOUR TASK**:
Analyze the aggregates, entities and requirements, then decide what files are needed based on the guidelines provided.

**REQUIRED OUTPUT FORMAT**:

## File Structure
Create mermaid diagrams representing the structure of the codebase you design.

- One set of diagrams for the main code
- One set of diagrams the test code

Each set should have
- A tree showing the high level root of main or test
- One tree per aggregate

## API Endpoints
Design the REST endpoints you think are appropriate. If not specified otherwise, make sure there are at least simple create (PUT), read (GET), list (GET), update (POST), delete (DELETE) endpoints present. Do not add any more unless there is something in the requirements that requires more.

## Architecture Decisions
[Explain your choices and rationale]

**GUIDANCE** (not requirements):
- **Essential**: Entity, Repository, Service, Controller for basic CRUD
- **Consider**: Separate DTOs if the entity has many fields or sensitive data
- **Consider**: Custom exceptions for better error handling
- **Consider**: Configuration classes if needed
- **Consider**: Integration tests for critical paths
- **Avoid**: Unnecessary abstractions, complex patterns, or files that don't add value

**Remember**: You decide what's needed based on the specific entities and use case. Don't generate files just because they're in the examples - only create what adds value to this particular application.

**Output**: write your results into a architecture.md file in the workspace. Do not repeat anything that is already in requirements.md, everybody always has access to both, they should complement each other, not repeat the exact same information.