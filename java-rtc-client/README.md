# IBM RTC Enumeration Client

A Java client program that demonstrates how to add enumerations using the IBM Rational Team Concert (RTC) client SDK.

## Overview

This project provides a complete example of how to:
- Connect to an IBM RTC server
- Create custom enumerations for work item attributes
- Add enumeration literals with proper sequencing
- Save enumerations to the RTC repository
- List existing enumerations in a project area

## Project Structure

```
java-rtc-client/
├── src/main/java/com/example/rtc/
│   ├── RTCEnumerationClient.java    # Main client class
│   ├── RTCConfig.java               # Configuration settings
│   └── EnumerationUtils.java        # Utility methods for enumeration operations
├── lib/                             # IBM RTC SDK JAR files (you need to add these)
├── pom.xml                          # Maven build configuration
└── README.md                        # This file
```

## Prerequisites

1. **Java Development Kit (JDK) 8 or higher**
2. **Apache Maven 3.6 or higher**
3. **IBM RTC Client SDK** - You need to obtain the following JAR files from your RTC installation:
   - `com.ibm.team.repository.common.jar`
   - `com.ibm.team.repository.client.jar`
   - `com.ibm.team.workitem.common.jar`
   - `com.ibm.team.workitem.client.jar`
   - `com.ibm.team.process.common.jar`

## Setup Instructions

### 1. Install IBM RTC SDK JARs

Create a `lib` directory in the project root and copy the required JAR files:

```bash
mkdir -p java-rtc-client/lib
```

Copy the following files to the `lib` directory:
- `com.ibm.team.repository.common.jar`
- `com.ibm.team.repository.client.jar`
- `com.ibm.team.workitem.common.jar`
- `com.ibm.team.workitem.client.jar`
- `com.ibm.team.process.common.jar`

### 2. Configure RTC Connection

Edit `src/main/java/com/example/rtc/RTCConfig.java` and update the following settings:

```java
// RTC Server Configuration
public static final String SERVER_URL = "https://your-rtc-server.com:9443/ccm";
public static final String USERNAME = "your-username";
public static final String PASSWORD = "your-password";
public static final String PROJECT_AREA_NAME = "Your Project Area";
```

### 3. Build the Project

```bash
cd java-rtc-client
mvn clean compile
```

### 4. Run the Client

```bash
mvn exec:java -Dexec.mainClass="com.example.rtc.RTCEnumerationClient"
```

Or create a JAR file and run it:

```bash
mvn clean package
java -jar target/rtc-enumeration-client-1.0.0.jar
```

## Features

### 1. Creating Enumerations

The client demonstrates how to create two types of enumerations:

**Priority Enumeration:**
- Critical
- High
- Medium
- Low

**Severity Enumeration:**
- Blocker
- Major
- Minor
- Trivial

### 2. Enumeration Management

The `EnumerationUtils` class provides utility methods for:
- Creating enumerations with literals
- Saving enumerations to the repository
- Finding existing enumerations
- Printing enumeration details

### 3. Configuration Management

The `RTCConfig` class centralizes all configuration settings:
- Server connection details
- Enumeration identifiers and names
- Literal definitions

## Code Examples

### Creating a Custom Enumeration

```java
// Define literals as [identifier, displayName] pairs
String[][] customLiterals = {
    {"value1", "Display Value 1"},
    {"value2", "Display Value 2"},
    {"value3", "Display Value 3"}
};

// Create and save the enumeration
IEnumeration<ILiteral> customEnum = EnumerationUtils.createAndSaveEnumeration(
    workItemCommon,
    projectArea,
    "custom.enumeration.id",
    "Custom Enumeration Display Name",
    customLiterals
);
```

### Adding the Enumeration to a Work Item Attribute

```java
// This would typically be done through the RTC web UI or additional API calls
// to create custom attributes and associate them with enumerations
```

## Error Handling

The client includes comprehensive error handling:
- Connection failures are logged and the application exits gracefully
- Enumeration creation errors are caught and logged as warnings
- Duplicate enumeration attempts are handled gracefully

## Logging

The application uses SLF4J for logging with the following levels:
- **INFO**: General operation progress
- **WARN**: Non-fatal issues (e.g., enumeration already exists)
- **ERROR**: Fatal errors that cause application termination
- **DEBUG**: Detailed operation information

## Customization

### Adding New Enumerations

1. Define the literals in `RTCConfig.java`
2. Add getter methods for the literal arrays
3. Create a new method in `RTCEnumerationClient.java` similar to `createPriorityEnumeration()`
4. Call the new method from `demonstrateEnumerationOperations()`

### Modifying Existing Enumerations

1. Update the literal definitions in `RTCConfig.java`
2. The next run will attempt to create the updated enumeration

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**: Ensure all RTC SDK JARs are in the `lib` directory
2. **Authentication Failed**: Verify username, password, and server URL
3. **Project Area Not Found**: Check the project area name is correct
4. **Permission Denied**: Ensure the user has permission to create enumerations

### Debug Mode

Enable debug logging by adding this to your JVM arguments:
```bash
-Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

## IBM RTC Version Compatibility

This client has been tested with:
- IBM RTC 7.0.x
- IBM RTC 6.0.x

For other versions, you may need to adjust the SDK JAR versions in `pom.xml`.

## Security Considerations

- Store credentials securely (consider using environment variables)
- Use HTTPS for RTC server connections
- Implement proper authentication mechanisms for production use

## License

This is example code for educational purposes. Consult your IBM RTC license for production use.

## Support

For issues related to:
- IBM RTC SDK: Consult IBM documentation and support
- This example code: Review the code comments and troubleshooting section