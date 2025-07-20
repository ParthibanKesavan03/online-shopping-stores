package com.example.rtc;

/**
 * Configuration class for RTC connection settings.
 * Modify these values to match your RTC environment.
 */
public class RTCConfig {
    
    // RTC Server Configuration
    public static final String SERVER_URL = "https://your-rtc-server.com:9443/ccm";
    public static final String USERNAME = "your-username";
    public static final String PASSWORD = "your-password";
    public static final String PROJECT_AREA_NAME = "Your Project Area";
    
    // Enumeration Configuration
    public static final String PRIORITY_ENUM_ID = "custom.priority";
    public static final String PRIORITY_ENUM_NAME = "Custom Priority";
    
    public static final String SEVERITY_ENUM_ID = "custom.severity";
    public static final String SEVERITY_ENUM_NAME = "Custom Severity";
    
    // Priority Literals
    public static final String[] PRIORITY_LITERALS = {
        "critical", "Critical",
        "high", "High", 
        "medium", "Medium",
        "low", "Low"
    };
    
    // Severity Literals  
    public static final String[] SEVERITY_LITERALS = {
        "blocker", "Blocker",
        "major", "Major",
        "minor", "Minor", 
        "trivial", "Trivial"
    };
    
    /**
     * Get priority literal pairs (identifier, display name)
     */
    public static String[][] getPriorityLiterals() {
        String[][] literals = new String[PRIORITY_LITERALS.length / 2][2];
        for (int i = 0; i < PRIORITY_LITERALS.length; i += 2) {
            literals[i / 2][0] = PRIORITY_LITERALS[i];     // identifier
            literals[i / 2][1] = PRIORITY_LITERALS[i + 1]; // display name
        }
        return literals;
    }
    
    /**
     * Get severity literal pairs (identifier, display name)
     */
    public static String[][] getSeverityLiterals() {
        String[][] literals = new String[SEVERITY_LITERALS.length / 2][2];
        for (int i = 0; i < SEVERITY_LITERALS.length; i += 2) {
            literals[i / 2][0] = SEVERITY_LITERALS[i];     // identifier
            literals[i / 2][1] = SEVERITY_LITERALS[i + 1]; // display name
        }
        return literals;
    }
}