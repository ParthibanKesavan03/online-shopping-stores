# WebLogic Deployment Guide - Role Validator Application

## Overview

This guide provides step-by-step instructions for deploying the Role Validator web application to Oracle WebLogic Server.

## Prerequisites

### Software Requirements
- Oracle WebLogic Server 12c or higher
- Java 8 or higher
- Maven 3.6+ (for building from source)
- Network access for REST API calls

### Server Requirements
- Minimum 2GB RAM allocated to WebLogic Server
- 100MB free disk space for application deployment
- Appropriate network configuration for external API access

## Pre-Deployment Steps

### 1. Build the Application

If building from source:

```bash
# Navigate to project directory
cd role-validator-webapp

# Clean and build
mvn clean package

# Verify WAR file creation
ls -la target/role-validator.war
```

### 2. Prepare WebLogic Environment

1. **Start WebLogic Server**:
   ```bash
   cd $DOMAIN_HOME
   ./startWebLogic.sh  # Linux/Mac
   # OR
   startWebLogic.cmd   # Windows
   ```

2. **Verify Admin Console Access**:
   - URL: http://[server]:7001/console
   - Login with administrator credentials

## Deployment Methods

### Method 1: Admin Console Deployment (Recommended)

#### Step 1: Access Admin Console
1. Open browser and navigate to WebLogic Admin Console
2. Login with administrator credentials

#### Step 2: Navigate to Deployments
1. Click on **"Deployments"** in the left navigation panel
2. Click **"Install"** button

#### Step 3: Locate Application
1. Click **"Upload your file(s)"**
2. Click **"Choose File"** and select `role-validator.war`
3. Click **"Next"**

#### Step 4: Configure Deployment
1. Select **"Install this deployment as an application"**
2. Click **"Next"**
3. Choose target servers (typically AdminServer for single-server setup)
4. Click **"Next"**

#### Step 5: Set Deployment Options
1. **Name**: role-validator (default)
2. **Security Model**: DD Only (recommended)
3. **Source Accessibility**: Use the uploaded file(s)
4. Click **"Next"**

#### Step 6: Complete Deployment
1. Review deployment settings
2. Click **"Finish"**
3. Wait for deployment completion

#### Step 7: Start Application
1. In Deployments list, find "role-validator"
2. Check the checkbox next to it
3. Click **"Start"** → **"Servicing all requests"**
4. Confirm the start operation

### Method 2: Auto-Deployment

#### For Development Environments Only

1. **Copy WAR file**:
   ```bash
   cp target/role-validator.war $DOMAIN_HOME/autodeploy/
   ```

2. **Monitor logs**:
   ```bash
   tail -f $DOMAIN_HOME/servers/AdminServer/logs/AdminServer.log
   ```

3. **Verify deployment**:
   - Look for successful deployment messages
   - Application should be automatically started

### Method 3: WLST Script Deployment

#### Step 1: Create Deployment Script

Create `deploy-role-validator.py`:

```python
# Connect to WebLogic Server
connect('weblogic','password','t3://localhost:7001')

# Deploy application
deploy('role-validator', '/path/to/role-validator.war', 
       targets='AdminServer', upload='true')

# Start application
start('role-validator', 'AdminServer')

# Disconnect
disconnect()
exit()
```

#### Step 2: Execute Script

```bash
java weblogic.WLST deploy-role-validator.py
```

## Post-Deployment Verification

### 1. Check Deployment Status

**Admin Console**:
1. Go to Deployments
2. Verify "role-validator" shows as "Active"
3. Check "Health" column shows "OK"

**Command Line**:
```bash
# Check if application is running
curl -I http://localhost:7001/role-validator/
```

### 2. Test Application Functionality

1. **Access Application**:
   ```
   URL: http://[server]:[port]/role-validator/
   ```

2. **Test Form Submission**:
   - Enter role name: "Test Role"
   - Enter description: "Test Description"
   - Click "Validate Role"
   - Verify successful response

3. **Test API Endpoint**:
   ```bash
   curl -X GET http://localhost:7001/role-validator/validate
   ```

### 3. Verify Logs

Check application logs for any errors:

```bash
# WebLogic Server log
tail -f $DOMAIN_HOME/servers/AdminServer/logs/AdminServer.log

# Look for application-specific entries
grep "role-validator" $DOMAIN_HOME/servers/AdminServer/logs/AdminServer.log
```

## Configuration

### 1. Context Root Modification

To change the context root from `/role-validator`:

1. **Edit weblogic.xml**:
   ```xml
   <context-root>/your-new-path</context-root>
   ```

2. **Redeploy application**

### 2. REST API Configuration

**Option 1: Environment Variables**
```bash
export REST_API_URL=https://your-api-endpoint.com/validate
```

**Option 2: System Properties**
Add to WebLogic startup script:
```bash
-Drest.api.url=https://your-api-endpoint.com/validate
```

**Option 3: Update Properties File**
Modify `application.properties` and rebuild.

### 3. Memory Configuration

For production deployments, adjust JVM settings:

```bash
# In setDomainEnv.sh
USER_MEM_ARGS="-Xms1024m -Xmx2048m -XX:PermSize=256m -XX:MaxPermSize=512m"
```

## Troubleshooting

### Common Issues and Solutions

#### 1. Application Not Starting

**Symptoms**: Deployment successful but application inactive

**Solutions**:
- Check server logs for errors
- Verify Java version compatibility
- Ensure all dependencies are available
- Check for port conflicts

#### 2. 404 Error When Accessing Application

**Symptoms**: "HTTP 404 Not Found"

**Solutions**:
- Verify correct URL: `http://server:port/role-validator/`
- Check if application is started in Admin Console
- Verify context root configuration

#### 3. REST API Connection Failures

**Symptoms**: Validation always fails with API errors

**Solutions**:
- Test API endpoint manually: `curl https://api-endpoint.com`
- Check network connectivity from WebLogic server
- Verify firewall rules
- Review proxy settings if applicable

#### 4. Memory Issues

**Symptoms**: OutOfMemoryError in logs

**Solutions**:
- Increase heap size: `-Xmx2048m`
- Monitor memory usage
- Consider connection pooling for REST calls

#### 5. Slow Performance

**Symptoms**: Application responds slowly

**Solutions**:
- Increase REST API timeout settings
- Monitor network latency to API endpoint
- Review WebLogic thread configuration
- Enable connection pooling

### Log Analysis

#### Key Log Messages to Monitor

**Successful Deployment**:
```
<Notice> <Deployer> <AdminServer> <role-validator> <Application Deployed>
```

**Application Started**:
```
<Notice> <Deployer> <AdminServer> <role-validator> <Application Started>
```

**REST API Calls**:
```
INFO [RoleValidationServlet] - Calling external API: [URL]
INFO [RoleValidationServlet] - API Response Status: 200
```

## Security Considerations

### 1. Production Deployment

- Use HTTPS for external REST API calls
- Enable authentication if required
- Configure appropriate security roles
- Review and harden WebLogic security settings

### 2. Network Security

- Restrict access to necessary ports only
- Use firewall rules for API endpoints
- Consider VPN for internal API communications

### 3. Application Security

- Validate and sanitize all user inputs
- Use parameterized queries if database access is added
- Implement rate limiting for API calls

## Monitoring and Maintenance

### 1. Health Checks

Create monitoring scripts:

```bash
#!/bin/bash
# health-check.sh
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:7001/role-validator/validate)
if [ $RESPONSE -eq 200 ]; then
    echo "Application is healthy"
    exit 0
else
    echo "Application health check failed: $RESPONSE"
    exit 1
fi
```

### 2. Log Rotation

Configure log rotation in WebLogic:
1. Go to Admin Console → Environment → Servers
2. Select your server → Logging
3. Configure log file rotation settings

### 3. Performance Monitoring

Monitor key metrics:
- Response times
- Memory usage
- Thread counts
- REST API call success rates

## Backup and Recovery

### 1. Application Backup

Backup the deployed WAR file:
```bash
cp $DOMAIN_HOME/servers/AdminServer/upload/role-validator.war /backup/location/
```

### 2. Configuration Backup

Backup WebLogic configuration:
```bash
tar -czf weblogic-config-backup.tar.gz $DOMAIN_HOME/config/
```

### 3. Recovery Process

1. Stop the application
2. Undeploy if necessary
3. Restore from backup
4. Redeploy and start

This completes the WebLogic deployment guide for the Role Validator application.