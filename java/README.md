# IBM RTC Bulk Category Adder

A Java client program for adding categories in bulk to IBM Rational Team Concert (RTC) using the IBM RTC Client SDK.

## Features

- Connects to IBM RTC server using username/password authentication
- Adds categories in bulk from a CSV file or uses default categories
- Checks for existing categories to avoid duplicates
- Provides detailed logging and error handling
- Supports custom category descriptions

## Prerequisites

1. **IBM RTC Client SDK**: Download and install the IBM RTC Client SDK from IBM
2. **Java 8 or higher**: Ensure Java is installed and configured
3. **Maven** (optional): For building with Maven

## Required IBM RTC SDK JARs

You need the following JAR files from IBM RTC Client SDK (exact names may vary by version):

- `teamserver-client.jar`
- `workitem-client.jar` 
- `process-client.jar`
- `repository-common.jar`
- `workitem-common.jar`
- `process-common.jar`
- Additional dependency JARs as required

## Setup

### Option 1: Manual Classpath Setup

1. Download IBM RTC Client SDK
2. Extract and locate the required JAR files
3. Compile the Java program:
   ```bash
   javac -cp "/path/to/rtc/sdk/*" RTCBulkCategoryAdder.java
   ```

### Option 2: Maven Setup

1. Update `pom.xml` with correct paths to your RTC SDK JARs
2. Set the `rtc.sdk.path` property to your RTC SDK installation directory
3. Build with Maven:
   ```bash
   mvn clean compile package
   ```

## Usage

### Command Line Syntax
```bash
java -cp "/path/to/rtc/sdk/*:." RTCBulkCategoryAdder <server_url> <username> <password> <project_area> [categories_file]
```

### Parameters

- `server_url`: IBM RTC server URL (e.g., `https://rtc.company.com:9443/ccm`)
- `username`: RTC username
- `password`: RTC password
- `project_area`: Name of the project area where categories will be added
- `categories_file`: Optional path to CSV file containing categories (default: `categories.csv`)

### Examples

1. **Using default categories:**
   ```bash
   java -cp "/opt/rtc/sdk/*:." RTCBulkCategoryAdder https://rtc.company.com:9443/ccm john.doe mypassword "My Project Area"
   ```

2. **Using custom categories file:**
   ```bash
   java -cp "/opt/rtc/sdk/*:." RTCBulkCategoryAdder https://rtc.company.com:9443/ccm john.doe mypassword "My Project Area" my_categories.csv
   ```

3. **With Maven-built JAR:**
   ```bash
   java -jar target/rtc-bulk-category-adder-1.0.0.jar https://rtc.company.com:9443/ccm john.doe mypassword "My Project Area"
   ```

## Categories CSV Format

The CSV file should contain category names and descriptions in the following format:

```csv
# Lines starting with # are comments
categoryName,categoryDescription
Bug,Software defects and issues that need to be fixed
Enhancement,Feature requests and improvements
Task,General tasks and activities
Story,User stories and requirements
Epic,Large features spanning multiple sprints
```

### CSV Rules
- First column: Category name (required)
- Second column: Category description (optional)
- Lines starting with `#` are treated as comments
- Empty lines are ignored
- Commas in descriptions should be escaped or the description should be quoted

## Default Categories

If no categories file is provided or the file cannot be read, the program will use these default categories:

- Bug: Software defects and issues
- Enhancement: Feature requests and improvements  
- Task: General tasks and activities
- Story: User stories and requirements
- Epic: Large features or initiatives

## Error Handling

The program includes comprehensive error handling:

- Authentication failures
- Network connectivity issues
- Project area not found
- Duplicate category detection
- Invalid CSV format
- Individual category creation failures

## Security Considerations

- **Credentials**: Never hardcode credentials in the source code
- **HTTPS**: Always use HTTPS for production RTC servers
- **Permissions**: Ensure the user account has appropriate permissions to create categories
- **Files**: Protect CSV files containing sensitive category information

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**: Ensure all RTC SDK JARs are in the classpath
2. **Authentication failed**: Verify username, password, and server URL
3. **Project area not found**: Check project area name spelling and case sensitivity
4. **Permission denied**: Verify user has category creation permissions in the project area
5. **Connection timeout**: Check network connectivity and firewall settings

### Debug Mode

Add JVM arguments for detailed logging:
```bash
java -Djava.util.logging.config.file=logging.properties -cp "/path/to/rtc/sdk/*:." RTCBulkCategoryAdder ...
```

## IBM RTC Version Compatibility

This program has been tested with:
- IBM RTC 6.x
- IBM RTC 7.x

For other versions, you may need to adjust the imports or API calls based on IBM RTC SDK documentation.

## License

This code is provided as an example and should be adapted according to your organization's requirements and IBM's licensing terms for RTC SDK usage.
