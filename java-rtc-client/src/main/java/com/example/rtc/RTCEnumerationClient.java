package com.example.rtc;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.ILoginHandler;
import com.ibm.team.repository.client.login.ILoginInfo;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IEnumeration;
import com.ibm.team.workitem.common.model.ILiteral;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.workitem.common.model.Identifier;

/**
 * IBM RTC Client to demonstrate adding enumerations to work item attributes.
 * This example shows how to create and configure enumerations for custom attributes.
 */
public class RTCEnumerationClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RTCEnumerationClient.class);
    
    // Configuration loaded from RTCConfig
    private static final String SERVER_URL = RTCConfig.SERVER_URL;
    private static final String USERNAME = RTCConfig.USERNAME;
    private static final String PASSWORD = RTCConfig.PASSWORD;
    private static final String PROJECT_AREA_NAME = RTCConfig.PROJECT_AREA_NAME;
    
    private ITeamRepository repository;
    private IWorkItemClient workItemClient;
    
    public static void main(String[] args) {
        RTCEnumerationClient client = new RTCEnumerationClient();
        try {
            client.run();
        } catch (Exception e) {
            logger.error("Error executing RTC enumeration client", e);
            System.exit(1);
        }
    }
    
    public void run() throws TeamRepositoryException {
        logger.info("Starting RTC Enumeration Client...");
        
        // Initialize and login to RTC
        initializeRTC();
        
        try {
            // Find the project area
            IProjectArea projectArea = findProjectArea(PROJECT_AREA_NAME);
            if (projectArea == null) {
                throw new RuntimeException("Project area not found: " + PROJECT_AREA_NAME);
            }
            
            // Demonstrate enumeration operations
            demonstrateEnumerationOperations(projectArea);
            
        } finally {
            // Logout and cleanup
            if (repository != null) {
                repository.logout();
                logger.info("Logged out from RTC repository");
            }
        }
    }
    
    /**
     * Initialize RTC platform and login to repository
     */
    private void initializeRTC() throws TeamRepositoryException {
        logger.info("Initializing RTC platform...");
        
        // Start the team platform
        TeamPlatform.startup();
        
        // Create repository connection
        repository = TeamPlatform.getTeamRepositoryService().getTeamRepository(SERVER_URL);
        
        // Create login handler
        ILoginHandler loginHandler = new ILoginHandler() {
            public ILoginInfo challenge(ITeamRepository repository) {
                return new UsernameAndPasswordLoginInfo(USERNAME, PASSWORD);
            }
        };
        
        repository.registerLoginHandler(loginHandler);
        
        // Login to repository
        repository.login(null);
        logger.info("Successfully logged into RTC repository: {}", SERVER_URL);
        
        // Get work item client
        workItemClient = (IWorkItemClient) repository.getClientLibrary(IWorkItemClient.class);
    }
    
    /**
     * Find project area by name
     */
    private IProjectArea findProjectArea(String projectAreaName) throws TeamRepositoryException {
        logger.info("Looking for project area: {}", projectAreaName);
        
        IProcessClientService processClient = (IProcessClientService) repository.getClientLibrary(IProcessClientService.class);
        URI uri = URI.create(projectAreaName.replaceAll(" ", "%20"));
        IProjectArea projectArea = (IProjectArea) processClient.findProcessArea(uri, null, null);
        
        if (projectArea != null) {
            logger.info("Found project area: {}", projectArea.getName());
        }
        
        return projectArea;
    }
    
    /**
     * Demonstrate various enumeration operations
     */
    private void demonstrateEnumerationOperations(IProjectArea projectArea) throws TeamRepositoryException {
        logger.info("Demonstrating enumeration operations...");
        
        // Example 1: Create a priority enumeration
        createPriorityEnumeration(projectArea);
        
        // Example 2: Create a severity enumeration
        createSeverityEnumeration(projectArea);
        
        // Example 3: Find and display existing enumerations
        listExistingEnumerations(projectArea);
    }
    
    /**
     * Create a custom priority enumeration
     */
    private void createPriorityEnumeration(IProjectArea projectArea) throws TeamRepositoryException {
        logger.info("Creating priority enumeration...");
        
        try {
            IWorkItemCommon workItemCommon = workItemClient.getWorkItemCommon();
            
            // Create and save the priority enumeration using utility method
            IEnumeration<ILiteral> priorityEnum = EnumerationUtils.createAndSaveEnumeration(
                workItemCommon,
                projectArea,
                RTCConfig.PRIORITY_ENUM_ID,
                RTCConfig.PRIORITY_ENUM_NAME,
                RTCConfig.getPriorityLiterals()
            );
            
            // Print details of created enumeration
            EnumerationUtils.printEnumerationDetails(priorityEnum);
            
        } catch (Exception e) {
            logger.warn("Priority enumeration might already exist or error occurred: {}", e.getMessage());
        }
    }
    
    /**
     * Create a custom severity enumeration
     */
    private void createSeverityEnumeration(IProjectArea projectArea) throws TeamRepositoryException {
        logger.info("Creating severity enumeration...");
        
        try {
            IWorkItemCommon workItemCommon = workItemClient.getWorkItemCommon();
            
            // Create and save the severity enumeration using utility method
            IEnumeration<ILiteral> severityEnum = EnumerationUtils.createAndSaveEnumeration(
                workItemCommon,
                projectArea,
                RTCConfig.SEVERITY_ENUM_ID,
                RTCConfig.SEVERITY_ENUM_NAME,
                RTCConfig.getSeverityLiterals()
            );
            
            // Print details of created enumeration
            EnumerationUtils.printEnumerationDetails(severityEnum);
                       
        } catch (Exception e) {
            logger.warn("Severity enumeration might already exist or error occurred: {}", e.getMessage());
        }
    }
    
    /**
     * List existing enumerations in the project area
     */
    private void listExistingEnumerations(IProjectArea projectArea) throws TeamRepositoryException {
        logger.info("Listing existing enumerations...");
        
        IWorkItemCommon workItemCommon = workItemClient.getWorkItemCommon();
        
        // Get all work item types
        IWorkItemType[] workItemTypes = workItemCommon.findWorkItemTypes(projectArea, null);
        
        for (IWorkItemType workItemType : workItemTypes) {
            logger.info("Work Item Type: {}", workItemType.getDisplayName());
            
            // Get all attributes for this work item type
            IAttribute[] attributes = workItemCommon.findAttributes(projectArea, null);
            
            for (IAttribute attribute : attributes) {
                if (attribute.getAttributeType().equals("enumeration")) {
                    logger.info("  - Enumeration Attribute: {} ({})", 
                               attribute.getDisplayName(), attribute.getIdentifier());
                    
                    // Get enumeration details
                    Object enumerationObj = workItemCommon.resolveEnumeration(attribute, null);
                    if (enumerationObj instanceof IEnumeration) {
                        IEnumeration<ILiteral> enumeration = (IEnumeration<ILiteral>) enumerationObj;
                        logger.info("    Enumeration: {} with {} literals", 
                                   enumeration.getDisplayName(), 
                                   enumeration.getEnumerationLiterals().size());
                        
                        // List all literals
                        for (ILiteral literal : enumeration.getEnumerationLiterals()) {
                            logger.info("      - {}: {}", 
                                       literal.getIdentifier2(), literal.getName());
                        }
                    }
                }
            }
        }
    }
}