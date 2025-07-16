# Role Validator Web Application

A Java web application designed to run on Oracle WebLogic Server that provides role validation functionality through REST API integration.

## Features

- **Single Page Interface**: Clean, modern web interface for role validation
- **REST API Integration**: Makes external API calls to validate roles
- **WebLogic Compatible**: Specifically designed for Oracle WebLogic Server deployment
- **Input Validation**: Client-side and server-side validation for role data
- **Error Handling**: Comprehensive error handling and user feedback
- **AJAX Interface**: Asynchronous form submission with loading indicators

## Application Structure

```
role-validator-webapp/
├── pom.xml                              # Maven project configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/servlet/
│   │   │       └── RoleValidationServlet.java  # Main servlet
│   │   ├── resources/
│   │   │   └── application.properties    # Application configuration
│   │   └── webapp/
│   │       ├── index.jsp                # Main page
│   │       ├── error.jsp                # Error page
│   │       └── WEB-INF/
│   │           ├── web.xml              # Web application descriptor
│   │           └── weblogic.xml         # WebLogic deployment descriptor
└── README.md
```

## Prerequisites

- **Java 8** or higher
- **Maven 3.6** or higher
- **Oracle WebLogic Server 12c** or higher
- **Internet connection** for REST API calls

## Building the Application

1. **Clone or download** the application source code
2. **Navigate** to the project directory
3. **Build** the WAR file using Maven:

```bash
mvn clean package
```

This will create `role-validator.war` in the `target/` directory.

## Deployment on WebLogic Server

### Method 1: WebLogic Admin Console

1. **Access** the WebLogic Admin Console (typically http://localhost:7001/console)
2. **Login** with administrator credentials
3. **Navigate** to Deployments
4. **Click** "Install"
5. **Upload** the `role-validator.war` file
6. **Follow** the deployment wizard:
   - Choose "Install this deployment as an application"
   - Set target servers (typically AdminServer for development)
   - Accept default settings
7. **Start** the application
8. **Access** the application at: `http://[server]:[port]/role-validator`

### Method 2: Auto-deployment (Development)

1. **Copy** the `role-validator.war` file to the WebLogic autodeploy directory:
   ```
   [DOMAIN_HOME]/autodeploy/
   ```
2. **Wait** for automatic deployment (check server logs)
3. **Access** the application at: `http://[server]:[port]/role-validator`

### Method 3: Command Line (WLST)

```bash
# Connect to WebLogic Server
java weblogic.WLST

# Connect to admin server
connect('username','password','t3://localhost:7001')

# Deploy the application
deploy('role-validator', '/path/to/role-validator.war', targets='AdminServer')

# Start the application
start('role-validator')
```

## Application Usage

1. **Open** the application in a web browser
2. **Enter** the following information:
   - **Role Name**: The name of the role to validate
   - **Role Description**: A description of the role
3. **Click** "Validate Role" button
4. **View** the validation results displayed on the page

## REST API Integration

The application makes HTTP POST requests to an external REST API endpoint to validate roles. The default configuration uses a demo API (JSONPlaceholder), but this can be customized in the `application.properties` file.

### Customizing the API Endpoint

Edit `src/main/resources/application.properties`:

```properties
# Change this to your actual REST API endpoint
rest.api.url=https://your-api-endpoint.com/validate-role
rest.api.timeout=30000
```

### API Request Format

The application sends the following JSON payload:

```json
{
  "title": "role_name",
  "body": "role_description", 
  "userId": 1
}
```

### Expected API Response

The external API should return a JSON response. The application handles various response formats and status codes.

## Configuration

### Application Properties

- `rest.api.url`: REST API endpoint URL
- `rest.api.timeout`: API call timeout in milliseconds
- `rest.api.retry.attempts`: Number of retry attempts for failed API calls
- `logging.level.*`: Logging configuration

### WebLogic Configuration

- **Context Root**: `/role-validator`
- **Session Timeout**: 30 minutes
- **Character Encoding**: UTF-8
- **JSP Compilation**: Optimized for production

## Troubleshooting

### Common Issues

1. **404 Error**: 
   - Check if the application is deployed and started
   - Verify the context root is correct (`/role-validator`)

2. **500 Error**:
   - Check WebLogic server logs for detailed error messages
   - Verify all dependencies are available in the classpath

3. **REST API Connection Issues**:
   - Check network connectivity
   - Verify the API endpoint URL in configuration
   - Check firewall settings

4. **Deployment Issues**:
   - Ensure WebLogic Server is running
   - Check if the WAR file is corrupted
   - Verify WebLogic version compatibility

### Log Files

- **WebLogic Server Log**: `[DOMAIN_HOME]/servers/[SERVER_NAME]/logs/[SERVER_NAME].log`
- **Application Log**: Check WebLogic server output for application-specific logs

## Development

### Building for Development

```bash
# Clean and compile
mvn clean compile

# Run tests (if any)
mvn test

# Package without running tests
mvn package -DskipTests

# Deploy to local WebLogic (if configured)
mvn weblogic:deploy
```

### IDE Setup

1. **Import** as Maven project
2. **Configure** Java 8 as project SDK
3. **Add** WebLogic Server libraries to classpath if needed
4. **Set** deployment target to local WebLogic instance

## Security Considerations

- **Input Validation**: All user inputs are validated on both client and server side
- **CSRF Protection**: Consider enabling CSRF protection for production environments
- **HTTPS**: Use HTTPS in production environments
- **Authentication**: Add authentication if required for your use case

## Support

For technical support or questions about this application:

1. **Check** the application logs for error details
2. **Review** the WebLogic Server documentation
3. **Verify** the REST API endpoint is accessible and working correctly

## License

This application is provided as-is for demonstration purposes.
