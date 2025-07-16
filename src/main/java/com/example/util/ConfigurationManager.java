package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for loading and accessing application properties
 */
public class ConfigurationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String CONFIG_FILE = "application.properties";
    private static ConfigurationManager instance;
    private Properties properties;
    
    private ConfigurationManager() {
        loadProperties();
    }
    
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        InputStream inputStream = null;
        
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuration loaded successfully from {}", CONFIG_FILE);
            } else {
                logger.warn("Configuration file {} not found, using default values", CONFIG_FILE);
                loadDefaultProperties();
            }
        } catch (IOException e) {
            logger.error("Error loading configuration from {}", CONFIG_FILE, e);
            loadDefaultProperties();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Error closing configuration file stream", e);
                }
            }
        }
    }
    
    private void loadDefaultProperties() {
        // Set default properties as fallback
        properties.setProperty("rest.api.url", "https://jsonplaceholder.typicode.com/posts");
        properties.setProperty("rest.api.timeout", "30000");
        properties.setProperty("rest.api.retry.attempts", "3");
        properties.setProperty("app.name", "Role Validator");
        properties.setProperty("app.version", "1.0.0");
        
        logger.info("Default configuration properties loaded");
    }
    
    /**
     * Get string property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get string property value with default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get integer property value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value for property {}: {}, using default: {}", 
                           key, value, defaultValue);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get boolean property value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
    
    /**
     * Get REST API URL
     */
    public String getRestApiUrl() {
        return getProperty("rest.api.url", "https://jsonplaceholder.typicode.com/posts");
    }
    
    /**
     * Get REST API timeout
     */
    public int getRestApiTimeout() {
        return getIntProperty("rest.api.timeout", 30000);
    }
    
    /**
     * Get REST API retry attempts
     */
    public int getRestApiRetryAttempts() {
        return getIntProperty("rest.api.retry.attempts", 3);
    }
    
    /**
     * Get application name
     */
    public String getApplicationName() {
        return getProperty("app.name", "Role Validator");
    }
    
    /**
     * Get application version
     */
    public String getApplicationVersion() {
        return getProperty("app.version", "1.0.0");
    }
}