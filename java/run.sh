#!/bin/bash

# IBM RTC Bulk Category Adder - Run Script
# Update RTC_SDK_PATH to point to your IBM RTC Client SDK installation

# Configuration
RTC_SDK_PATH="/opt/ibm/rtc/sdk"  # Update this path
CLASS_NAME="RTCBulkCategoryAdder"

echo "IBM RTC Bulk Category Adder - Run Script"
echo "========================================"

# Check if RTC SDK path exists
if [ ! -d "$RTC_SDK_PATH" ]; then
    echo "ERROR: RTC SDK path not found: $RTC_SDK_PATH"
    echo "Please update RTC_SDK_PATH in this script to point to your IBM RTC Client SDK installation"
    exit 1
fi

# Check if class file exists
if [ ! -f "$CLASS_NAME.class" ]; then
    echo "ERROR: Compiled class file not found: $CLASS_NAME.class"
    echo "Please compile the program first using: ./compile.sh"
    exit 1
fi

# Check command line arguments
if [ $# -lt 4 ]; then
    echo "Usage: $0 <server_url> <username> <password> <project_area> [categories_file]"
    echo ""
    echo "Parameters:"
    echo "  server_url     - IBM RTC server URL (e.g., https://rtc.company.com:9443/ccm)"
    echo "  username       - RTC username"
    echo "  password       - RTC password" 
    echo "  project_area   - Name of the project area"
    echo "  categories_file - Optional path to CSV file (default: categories.csv)"
    echo ""
    echo "Example:"
    echo "  $0 https://rtc.company.com:9443/ccm john.doe mypassword \"My Project Area\""
    echo "  $0 https://rtc.company.com:9443/ccm john.doe mypassword \"My Project Area\" my_categories.csv"
    exit 1
fi

echo "RTC SDK Path: $RTC_SDK_PATH"
echo "Running: $CLASS_NAME"
echo "Arguments: $@"
echo ""

# Run the Java program
java -cp "$RTC_SDK_PATH/*:." "$CLASS_NAME" "$@"

exit_code=$?
echo ""
if [ $exit_code -eq 0 ]; then
    echo "✓ Program completed successfully!"
else
    echo "✗ Program failed with exit code: $exit_code"
fi

exit $exit_code
