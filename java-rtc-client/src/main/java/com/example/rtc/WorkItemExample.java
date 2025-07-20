package com.example.rtc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.IWorkItemWorkingCopyManager;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IAttribute;
import com.ibm.team.workitem.common.model.IEnumeration;
import com.ibm.team.workitem.common.model.ILiteral;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemType;

/**
 * Example class demonstrating how to create work items and set enumeration values.
 * This class shows how to use the enumerations created by RTCEnumerationClient.
 */
public class WorkItemExample {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkItemExample.class);
    
    private IWorkItemClient workItemClient;
    private IWorkItemCommon workItemCommon;
    
    public WorkItemExample(IWorkItemClient workItemClient) {
        this.workItemClient = workItemClient;
        this.workItemCommon = workItemClient.getWorkItemCommon();
    }
    
    /**
     * Create a sample work item with enumeration values
     * 
     * @param projectArea The project area to create the work item in
     * @return The created work item
     * @throws TeamRepositoryException If creation fails
     */
    public IWorkItem createWorkItemWithEnumerations(IProjectArea projectArea) throws TeamRepositoryException {
        logger.info("Creating work item with enumeration values...");
        
        // Find a work item type (e.g., "Task", "Defect", "Story")
        IWorkItemType workItemType = findWorkItemType(projectArea, "Task");
        if (workItemType == null) {
            throw new RuntimeException("Work item type 'Task' not found");
        }
        
        // Create work item working copy manager
        IWorkItemWorkingCopyManager workingCopyManager = workItemClient.getWorkItemWorkingCopyManager();
        workingCopyManager.connect(workItemClient.getTeamRepository(), null, null);
        
        try {
            // Create new work item
            IWorkItem workItem = workingCopyManager.createWorkItem(workItemType);
            
            // Set basic attributes
            workItem.setHTMLSummary(workItemCommon.createHTMLSummary(
                "<b>Sample Work Item with Custom Enumerations</b>", 
                workItem, 
                null));
            
            workItem.setHTMLDescription(workItemCommon.createHTMLDescription(
                "This work item demonstrates the use of custom priority and severity enumerations.", 
                workItem, 
                null));
            
            // Set enumeration values
            setEnumerationValue(workItem, projectArea, "priority", RTCConfig.PRIORITY_ENUM_ID, "high");
            setEnumerationValue(workItem, projectArea, "severity", RTCConfig.SEVERITY_ENUM_ID, "major");
            
            // Save the work item
            workingCopyManager.save(null);
            
            logger.info("Created work item #{} with enumeration values", workItem.getId());
            return workItem;
            
        } finally {
            workingCopyManager.disconnect();
        }
    }
    
    /**
     * Find a work item type by name
     * 
     * @param projectArea The project area
     * @param typeName The work item type name
     * @return The work item type, or null if not found
     * @throws TeamRepositoryException If search fails
     */
    private IWorkItemType findWorkItemType(IProjectArea projectArea, String typeName) throws TeamRepositoryException {
        IWorkItemType[] workItemTypes = workItemCommon.findWorkItemTypes(projectArea, null);
        
        for (IWorkItemType type : workItemTypes) {
            if (typeName.equals(type.getIdentifier()) || typeName.equals(type.getDisplayName())) {
                logger.debug("Found work item type: {}", type.getDisplayName());
                return type;
            }
        }
        
        logger.warn("Work item type '{}' not found", typeName);
        return null;
    }
    
    /**
     * Set an enumeration value on a work item attribute
     * 
     * @param workItem The work item to modify
     * @param projectArea The project area
     * @param attributeId The attribute identifier
     * @param enumerationId The enumeration identifier
     * @param literalId The literal identifier to set
     * @throws TeamRepositoryException If setting the value fails
     */
    private void setEnumerationValue(
            IWorkItem workItem, 
            IProjectArea projectArea,
            String attributeId, 
            String enumerationId, 
            String literalId) throws TeamRepositoryException {
        
        logger.debug("Setting enumeration value: {}={} on attribute {}", 
                    enumerationId, literalId, attributeId);
        
        // Find the attribute
        IAttribute attribute = findAttribute(projectArea, attributeId);
        if (attribute == null) {
            logger.warn("Attribute '{}' not found, skipping enumeration value setting", attributeId);
            return;
        }
        
        // Get the enumeration
        IEnumeration<ILiteral> enumeration = workItemCommon.resolveEnumeration(attribute, null);
        if (enumeration == null) {
            logger.warn("Enumeration not found for attribute '{}'", attributeId);
            return;
        }
        
        // Find the literal
        ILiteral literal = findLiteralById(enumeration, literalId);
        if (literal == null) {
            logger.warn("Literal '{}' not found in enumeration '{}'", literalId, enumerationId);
            return;
        }
        
        // Set the value
        workItem.setValue(attribute, literal.getIdentifier2());
        
        logger.debug("Set {} = {} on work item", attributeId, literal.getName());
    }
    
    /**
     * Find an attribute by identifier
     * 
     * @param projectArea The project area
     * @param attributeId The attribute identifier
     * @return The attribute, or null if not found
     * @throws TeamRepositoryException If search fails
     */
    private IAttribute findAttribute(IProjectArea projectArea, String attributeId) throws TeamRepositoryException {
        IAttribute[] attributes = workItemCommon.findAttributes(projectArea, null);
        
        for (IAttribute attribute : attributes) {
            if (attributeId.equals(attribute.getIdentifier())) {
                return attribute;
            }
        }
        
        return null;
    }
    
    /**
     * Find a literal by identifier in an enumeration
     * 
     * @param enumeration The enumeration to search
     * @param literalId The literal identifier
     * @return The literal, or null if not found
     */
    private ILiteral findLiteralById(IEnumeration<ILiteral> enumeration, String literalId) {
        for (ILiteral literal : enumeration.getEnumerationLiterals()) {
            if (literalId.equals(literal.getIdentifier2())) {
                return literal;
            }
        }
        return null;
    }
    
    /**
     * Print work item details including enumeration values
     * 
     * @param workItem The work item to print
     * @param projectArea The project area
     * @throws TeamRepositoryException If accessing work item data fails
     */
    public void printWorkItemDetails(IWorkItem workItem, IProjectArea projectArea) throws TeamRepositoryException {
        logger.info("Work Item Details:");
        logger.info("  ID: {}", workItem.getId());
        logger.info("  Type: {}", workItem.getWorkItemType());
        logger.info("  Summary: {}", workItem.getHTMLSummary().getPlainText());
        
        // Print enumeration values
        printEnumerationAttributeValue(workItem, projectArea, "priority");
        printEnumerationAttributeValue(workItem, projectArea, "severity");
    }
    
    /**
     * Print the value of an enumeration attribute
     * 
     * @param workItem The work item
     * @param projectArea The project area
     * @param attributeId The attribute identifier
     * @throws TeamRepositoryException If accessing the attribute fails
     */
    private void printEnumerationAttributeValue(
            IWorkItem workItem, 
            IProjectArea projectArea, 
            String attributeId) throws TeamRepositoryException {
        
        IAttribute attribute = findAttribute(projectArea, attributeId);
        if (attribute == null) {
            return;
        }
        
        Object value = workItem.getValue(attribute);
        if (value != null) {
            IEnumeration<ILiteral> enumeration = workItemCommon.resolveEnumeration(attribute, null);
            if (enumeration != null) {
                ILiteral literal = findLiteralById(enumeration, value.toString());
                if (literal != null) {
                    logger.info("  {}: {} ({})", 
                               attribute.getDisplayName(), 
                               literal.getName(), 
                               literal.getIdentifier2());
                }
            }
        }
    }
}