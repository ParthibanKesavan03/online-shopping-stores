#!/bin/bash

# Role Validator Web Application Build Script
# This script builds the Java web application for WebLogic deployment

echo "======================================"
echo "Role Validator Web Application Builder"
echo "======================================"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven 3.6+ and try again"
    exit 1
fi

# Check Java version
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 8+ and try again"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1-2)
echo "Java Version: $JAVA_VERSION"

# Clean previous builds
echo ""
echo "Cleaning previous builds..."
mvn clean

# Validate Maven project
echo ""
echo "Validating Maven project..."
mvn validate

if [ $? -ne 0 ]; then
    echo "ERROR: Maven project validation failed"
    exit 1
fi

# Compile the application
echo ""
echo "Compiling Java sources..."
mvn compile

if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed"
    exit 1
fi

# Run tests (if any)
echo ""
echo "Running tests..."
mvn test

# Package the application
echo ""
echo "Packaging WAR file..."
mvn package

if [ $? -ne 0 ]; then
    echo "ERROR: Packaging failed"
    exit 1
fi

# Check if WAR file was created
WAR_FILE="target/role-validator.war"
if [ -f "$WAR_FILE" ]; then
    echo ""
    echo "SUCCESS: Application built successfully!"
    echo "WAR file location: $WAR_FILE"
    echo "WAR file size: $(du -h $WAR_FILE | cut -f1)"
    echo ""
    echo "Deployment Instructions:"
    echo "1. Copy $WAR_FILE to your WebLogic autodeploy directory, OR"
    echo "2. Deploy via WebLogic Admin Console (http://localhost:7001/console)"
    echo "3. Access application at: http://[server]:[port]/role-validator/"
    echo ""
    echo "For detailed deployment instructions, see deployment-guide.md"
else
    echo "ERROR: WAR file was not created"
    exit 1
fi

echo "Build completed successfully!"