#!/bin/bash

# Pre-commit script that runs tests
# This script is called by the git pre-commit hook

set -e

echo "Running tests before commit..."

# Change to the office-library-app directory and run tests
cd office-library-app
./gradlew test

echo "All tests passed!"