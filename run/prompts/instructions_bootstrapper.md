
Bootstrap the basic structure of a Spring Boot application for our CRUD application, and ONLY the basic structure, do NOT generate any domain code yet.

Derive the application name from the domain described originally by the user. Derive a package name as well, package root should be `com.gen.example`.

## 1. Bootstrap via scripts

Use the scripts in the scripts folder to do the bootstrapping.

`./scripts/bootstrap.sh`

If you find any errors from running the script, try to fix them.


## 2. Create a README

Create a `README.md` file in the root directory with a short description of the application and how to run and test it.

## 3. Create a sensible .gitignore file

## 5. Initialise dev tools

Create a folder `dev-tools` that will contain all our local dev tools. For now, we want this structure:

```
dev-tools
  - git
    - .githooks
        - pre-commit-git
    - pre-commit.sh    
```
The pre-commit.sh should only contain `./gradlew test` for now.
