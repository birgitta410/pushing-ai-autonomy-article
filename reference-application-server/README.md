# reference-application-server MCP Server

A Model Context Protocol server that provides code examples from a Spring Boot reference application.

## Features

### Tools
The server provides 7 tools that return the contents of actual code files:

- `get_sample_controller` - Returns a complete Spring Boot REST controller with CRUD operations
- `get_sample_entity` - Returns a JPA entity class with validation annotations and relationships  
- `get_sample_repository` - Returns a Spring Data JPA repository interface with custom queries
- `get_sample_service` - Returns a Spring service class with business logic and transaction management
- `get_sample_controller_test` - Returns a controller test class using MockMvc
- `get_sample_repository_test` - Returns a repository test class using @DataJpaTest
- `get_sample_service_test` - Returns a service test class using Mockito

### File Validation
The server validates at startup that all configured code example files exist. If any files are missing, the server will fail to start with a clear error message listing the missing files.

## Configuration

The server uses the included `wine-tracker` application as its reference project. The configuration is automatically set to point to the `wine-tracker` directory within the MCP server, making it portable and requiring no additional setup.

### Configuration Files Structure

The server automatically resolves these paths relative to the included `wine-tracker` directory, so no additional configuration is required.

## Development

Install dependencies:
```bash
npm install
```

Build the server:
```bash
npm run build
```

For development with auto-rebuild:
```bash
npm run watch
```

## Configuration in MCP client

```json
{
  "mcpServers": {
    "reference-application": {
      "command": "node",
      "args": ["/path/to/reference-application-server/build/index.js"]
    }
  }
}
```

