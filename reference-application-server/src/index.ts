#!/usr/bin/env node

/**
 * MCP server that provides file paths to code examples from a specific project.
 * This server exposes tools to get file paths to sample code for controllers, 
 * entities, repositories, services, and tests from the wine tracking project.
 */

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import * as fs from "fs";
import * as path from "path";
import { fileURLToPath } from "url";
import { execSync } from "child_process";

/**
 * Shared configuration interface for code example paths
 */
interface SharedConfig {
  codeExamplePaths: {
    controller: string;
    entity: string;
    repository: string;
    service: string;
    controllerTest: string;
    repositoryTest: string;
    serviceTest: string;
  };
}

/**
 * Combined configuration interface
 */
interface Config extends SharedConfig {
  rootPath: string;
}

/**
 * Get the directory where this script is located
 */
function getScriptDirectory(): string {
  const __filename = fileURLToPath(import.meta.url);
  return path.dirname(__filename);
}

/**
 * Load shared configuration from config.json file
 */
function loadSharedConfig(): SharedConfig {
  try {

    const scriptDir = getScriptDirectory();
    const configPath = path.join(path.dirname(scriptDir), 'config.json');

    const configData = fs.readFileSync(configPath, 'utf8');
    return JSON.parse(configData) as SharedConfig;
  } catch (error) {
    throw new Error(`Failed to load shared configuration from config.json: ${error instanceof Error ? error.message : String(error)}`);
  }
}

/**
 * Get the wine-tracker directory path relative to the MCP server
 */
function getWineTrackerPath(): string {
  const scriptDir = getScriptDirectory();
  const serverDir = path.dirname(scriptDir); // Go up from build/ to reference-application-server/
  return path.join(serverDir, 'wine-tracker');
}

/**
 * Load configuration with wine-tracker as the fixed root path
 */
function loadConfig(): Config {
  const sharedConfig = loadSharedConfig();
  const rootPath = getWineTrackerPath();
  
  return {
    ...sharedConfig,
    rootPath
  };
}

/**
 * Build absolute file paths from configuration
 */
function buildCodeExamplePaths(config: Config): Record<string, string> {
  const paths: Record<string, string> = {};
  
  for (const [key, relativePath] of Object.entries(config.codeExamplePaths)) {
    paths[key] = path.join(config.rootPath, relativePath);
  }
  
  return paths;
}

// Load configuration and build paths
const config = loadConfig();
const codeExamplePaths = buildCodeExamplePaths(config);

/**
 * Validate that all required code example files exist
 * Throws an error if any files are missing
 */
function validateCodeExampleFiles() {
  const missingFiles: string[] = [];
  
  for (const [type, filePath] of Object.entries(codeExamplePaths)) {
    if (!fs.existsSync(filePath)) {
      missingFiles.push(`${type}: ${filePath}`);
    }
  }
  
  if (missingFiles.length > 0) {
    throw new Error(`Code example files not found:\n${missingFiles.join('\n')}`);
  }
  
}

/**
 * Create an MCP server with tools for getting code example file paths
 */
const server = new Server(
  {
    name: "reference-application-server",
    version: "0.1.0",
  },
  {
    capabilities: {
      tools: {},
    },
  }
);

/**
 * Handler that lists available tools for getting code example file paths
 */
server.setRequestHandler(ListToolsRequestSchema, async () => {
  return {
    tools: [
      {
        name: "get_sample_controller",
        description: "Get the contents of a sample Spring Boot REST controller class",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_sample_entity",
        description: "Get the contents of a sample JPA entity class",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_sample_repository",
        description: "Get the contents of a sample Spring Data JPA repository interface",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_sample_service",
        description: "Get the contents of a sample Spring service class",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_sample_controller_test",
        description: "Get the contents of a sample controller test class",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_sample_repository_test",
        description: "Get the contents of a sample repository test class",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_sample_service_test",
        description: "Get the contents of a sample service test class",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_latest_diff",
        description: "Get the diff information of the latest commit from the root repository",
        inputSchema: {
          type: "object",
          properties: {},
          required: []
        }
      },
      {
        name: "get_commit_diff",
        description: "Get the diff information for a specific commit SHA from the root repository",
        inputSchema: {
          type: "object",
          properties: {
            sha: {
              type: "string",
              description: "The commit SHA to get the diff for"
            }
          },
          required: ["sha"]
        }
      }
    ]
  };
});

/**
 * Handler for all the code example tools
 */
server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const toolName = request.params.name;
  
  let filePath: string;
  let description: string;
  
  switch (toolName) {
    case "get_sample_controller":
      filePath = codeExamplePaths.controller;
      description = "Sample Spring Boot REST controller with CRUD operations";
      break;
      
    case "get_sample_entity":
      filePath = codeExamplePaths.entity;
      description = "Sample JPA entity with validation annotations and relationships";
      break;
      
    case "get_sample_repository":
      filePath = codeExamplePaths.repository;
      description = "Sample Spring Data JPA repository with custom queries";
      break;
      
    case "get_sample_service":
      filePath = codeExamplePaths.service;
      description = "Sample Spring service with business logic and transaction management";
      break;
      
    case "get_sample_controller_test":
      filePath = codeExamplePaths.controllerTest;
      description = "Sample controller test class";
      break;
      
    case "get_sample_repository_test":
      filePath = codeExamplePaths.repositoryTest;
      description = "Sample repository test class";
      break;
      
    case "get_sample_service_test":
      filePath = codeExamplePaths.serviceTest;
      description = "Sample service test class";
      break;
      
    case "get_latest_diff":
      try {
        // Get the latest commit hash
        const latestCommitHash = execSync('git rev-parse HEAD', {
          cwd: config.rootPath,
          encoding: 'utf8'
        }).trim();
        
        // Get the diff of the latest commit
        const diffOutput = execSync(`git show ${latestCommitHash}`, {
          cwd: config.rootPath,
          encoding: 'utf8'
        });
        
        return {
          content: [{
            type: "text",
            text: `Latest commit diff from repository: ${config.rootPath}\n\nCommit: ${latestCommitHash}\n\n\`\`\`diff\n${diffOutput}\n\`\`\``
          }]
        };
      } catch (error) {
        throw new Error(`Failed to get latest commit diff: ${error instanceof Error ? error.message : String(error)}`);
      }
      
    case "get_commit_diff":
      try {
        const sha = request.params.arguments?.sha;
        if (!sha || typeof sha !== 'string') {
          throw new Error('SHA parameter is required and must be a string');
        }
        
        // Validate SHA format (basic check for hexadecimal string)
        if (!/^[a-fA-F0-9]+$/.test(sha)) {
          throw new Error('Invalid SHA format');
        }
        
        // Get the diff of the specified commit
        const diffOutput = execSync(`git show ${sha}`, {
          cwd: config.rootPath,
          encoding: 'utf8'
        });
        
        return {
          content: [{
            type: "text",
            text: `Commit diff from repository: ${config.rootPath}\n\nCommit: ${sha}\n\n\`\`\`diff\n${diffOutput}\n\`\`\``
          }]
        };
      } catch (error) {
        throw new Error(`Failed to get commit diff for SHA ${request.params.arguments?.sha}: ${error instanceof Error ? error.message : String(error)}`);
      }
      
    default:
      throw new Error(`Unknown tool: ${toolName}`);
  }

  try {
    // Read the file contents
    const fileContents = fs.readFileSync(filePath, 'utf8');
    
    return {
      content: [{
        type: "text",
        text: `${description}\n\nFile: ${filePath}\n\n\`\`\`java\n${fileContents}\n\`\`\``
      }]
    };
  } catch (error) {
    throw new Error(`Failed to read file ${filePath}: ${error instanceof Error ? error.message : String(error)}`);
  }
});

/**
 * Start the server using stdio transport
 */
async function main() {
  // Validate that all code example files exist before starting the server
  validateCodeExampleFiles();
  
  const transport = new StdioServerTransport();
  await server.connect(transport);
}

main().catch((error) => {
  console.error("Server error:", error);
  process.exit(1);
});
