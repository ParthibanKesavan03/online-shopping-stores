# IBM RTC SDK JAR Files

This directory should contain the IBM RTC Client SDK JAR files required for the enumeration client to work.

## Required JAR Files

You need to copy the following JAR files from your IBM RTC installation to this directory:

1. `com.ibm.team.repository.common.jar`
2. `com.ibm.team.repository.client.jar`
3. `com.ibm.team.workitem.common.jar`
4. `com.ibm.team.workitem.client.jar`
5. `com.ibm.team.process.common.jar`

## Where to Find These Files

These JAR files are typically located in your RTC installation directory:

### RTC Eclipse Client
```
<RTC_INSTALL_DIR>/eclipse/plugins/
```

### RTC Server Installation
```
<RTC_SERVER_DIR>/server/conf/
```

### RTC Plain Java Client Libraries
If you have the RTC Plain Java Client Libraries package, they will be in:
```
<RTC_CLIENT_LIBS>/
```

## File Structure After Setup

After copying the required files, this directory should look like:

```
lib/
├── com.ibm.team.repository.common.jar
├── com.ibm.team.repository.client.jar
├── com.ibm.team.workitem.common.jar
├── com.ibm.team.workitem.client.jar
├── com.ibm.team.process.common.jar
└── README.md (this file)
```

## Version Compatibility

- For RTC 7.x: Use JARs from RTC 7.x installation
- For RTC 6.x: Use JARs from RTC 6.x installation
- Ensure all JARs are from the same RTC version

## Licensing

These JAR files are proprietary IBM software. Ensure you have appropriate licenses to use them in your environment.