#!/bin/bash

# IBM RTC Bulk Category Adder - Compilation Script
# Update RTC_SDK_PATH to point to your IBM RTC Client SDK installation

# Configuration
RTC_SDK_PATH="/opt/ibm/rtc/sdk"  # Update this path
JAVA_FILE="RTCBulkCategoryAdder.java"
CLASS_NAME="RTCBulkCategoryAdder"

echo "IBM RTC Bulk Category Adder - Compilation Script"
echo "================================================="

# Check if RTC SDK path exists
if [ ! -d "$RTC_SDK_PATH" ]; then
    echo "ERROR: RTC SDK path not found: $RTC_SDK_PATH"
    echo "Please update RTC_SDK_PATH in this script to point to your IBM RTC Client SDK installation"
    exit 1
fi

# Check if Java file exists
if [ ! -f "$JAVA_FILE" ]; then
    echo "ERROR: Java source file not found: $JAVA_FILE"
    exit 1
fi

echo "RTC SDK Path: $RTC_SDK_PATH"
echo "Compiling: $JAVA_FILE"

# Compile the Java program
javac -cp "$RTC_SDK_PATH/*" "$JAVA_FILE"

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "To run the program:"
    echo "java -cp \"$RTC_SDK_PATH/*:.\" $CLASS_NAME <server_url> <username> <password> <project_area> [categories_file]"
    echo ""
    echo "Example:"
    echo "java -cp \"$RTC_SDK_PATH/*:.\" $CLASS_NAME https://rtc.company.com:9443/ccm john.doe mypassword \"My Project Area\""
else
    echo "✗ Compilation failed!"
    echo "Please check:"
    echo "1. RTC SDK path is correct and contains required JAR files"
    echo "2. Java source code has no syntax errors"
    echo "3. All required IBM RTC SDK dependencies are available"
    exit 1
fi
