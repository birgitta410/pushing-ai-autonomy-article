
Companion repository for 

https://martinfowler.com/articles/pushing-ai-autonomy.html

--> A set of prompts to generate a Spring Boot application. The workflow represents an example set of conventions and practices for such an application, to see if AI can follow them. E.g., one such practice is to use DTOs and mappers, another one to use Lombok.

## Set up

- Install the Roo Code extension 
- Build the MCP server: `cd reference-application-server && npm install && npm run build`
- Open the "run" directory as a workspace root in VS Code
- Double check that "reference-application" MCP server is successfully configured in Roo Code by checking Roo's list of configured MCP servers (it should be pulled from the `run/.roo` directory)

## Run

Start a new chat in Roo Code and select Roo Code's "Orchestrator" mode

Prompt:
```
@/prompts 

CRUD workflow described in @/prompts/_orchestrator.md 

Build a CRUD application to ...

```

Add your own domain, e.g. "track books in an office library", or "maintain a simple product catalog". 

### Adjusting complexity 

To control the level of complexity, you can manually edit the `requirements.md` document that gets created by the first workflow step, the requirements analyst. The workflow should stop at that point and give you a chance to review. 

#### Number of entities

If you see just one entity, or you see way too many, you can either edit or restart, to not make it too easy or too complex. If you just want to get an idea of how this typically ran for us, we recommend ~3 entities.

#### Complexity of endpoints

Even though the instructions clearly state to only create "CRUD" endpoints, frequently AI will already go overboard in this step and add additional search endpoints to the API design. You can reduce complexity by removing some of those excess endpoints - or try your luck and leave them in and see how well AI copes.

### Notes about what's in the prompts

Beyond the expectation that the build and all the tests are running, the following are examples of instructions that we put into the prompts that we explicitly wanted to see happen. They are representative for patterns that one might want to control with AI. Even if you disagree with some of them, or think they are not important, think of them as our test cases to review AI's ability to follow instructions like this, even if yours might be different.

- We want only the code necessary to build Create, Read, Update, Delete functionality - nothing more (watch for that in the `requirements.md`, Controllers, and Repositories)
- We ask for grouping the entities into sensible aggregates (watch for that in `requirements.md`, and it will ultimately influence the package structure). This is less observable when you limit the complexity to a low number of entities.
- We ask it to use DTOs and mappers
- We ask for constructor injection
- Via the reference application samples, we expect the use of Lombok, `jakarta.persistence` (over `javax.persistence`) and `@MockitoBean` in Controller tests (instead of the deprecated `@MockBean` annotation)
- We expect tests to be written for all classes
