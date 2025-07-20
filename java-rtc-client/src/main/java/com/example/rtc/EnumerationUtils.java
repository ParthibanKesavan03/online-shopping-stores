package com.example.rtc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IEnumeration;
import com.ibm.team.workitem.common.model.ILiteral;

/**
 * Utility class for enumeration operations in IBM RTC.
 * Provides helper methods for creating and managing enumerations.
 */
public class EnumerationUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(EnumerationUtils.class);
    
    /**
     * Create an enumeration with the specified literals
     * 
     * @param workItemCommon The work item common service
     * @param projectArea The project area
     * @param enumId The enumeration identifier
     * @param enumDisplayName The enumeration display name
     * @param literals Array of [identifier, displayName] pairs
     * @return The created enumeration
     * @throws TeamRepositoryException If creation fails
     */
    public static IEnumeration<ILiteral> createEnumeration(
            IWorkItemCommon workItemCommon,
            IProjectArea projectArea,
            String enumId,
            String enumDisplayName,
            String[][] literals) throws TeamRepositoryException {
        
        logger.info("Creating enumeration: {} ({})", enumDisplayName, enumId);
        
        // Create enumeration
        IEnumeration<ILiteral> enumeration = workItemCommon.createEnumeration(projectArea);
        enumeration.setDisplayName(enumDisplayName);
        enumeration.setIdentifier(enumId);
        
        // Create literals
        List<ILiteral> literalList = new ArrayList<>();
        for (int i = 0; i < literals.length; i++) {
            String identifier = literals[i][0];
            String displayName = literals[i][1];
            
            ILiteral literal = workItemCommon.createLiteral(enumeration);
            literal.setIdentifier2(identifier);
            literal.setName(displayName);
            literal.setSequenceValue(i + 1);
            
            literalList.add(literal);
            
            logger.debug("Created literal: {} ({})", displayName, identifier);
        }
        
        // Add literals to enumeration
        enumeration.getEnumerationLiterals().addAll(literalList);
        
        return enumeration;
    }
    
    /**
     * Save an enumeration to the repository
     * 
     * @param workItemCommon The work item common service
     * @param enumeration The enumeration to save
     * @throws TeamRepositoryException If save fails
     */
    public static void saveEnumeration(
            IWorkItemCommon workItemCommon,
            IEnumeration<ILiteral> enumeration) throws TeamRepositoryException {
        
        logger.info("Saving enumeration: {}", enumeration.getDisplayName());
        
        workItemCommon.saveEnumeration(enumeration, null);
        
        logger.info("Successfully saved enumeration with {} literals", 
                   enumeration.getEnumerationLiterals().size());
    }
    
    /**
     * Create and save an enumeration in one operation
     * 
     * @param workItemCommon The work item common service
     * @param projectArea The project area
     * @param enumId The enumeration identifier
     * @param enumDisplayName The enumeration display name
     * @param literals Array of [identifier, displayName] pairs
     * @return The created and saved enumeration
     * @throws TeamRepositoryException If creation or save fails
     */
    public static IEnumeration<ILiteral> createAndSaveEnumeration(
            IWorkItemCommon workItemCommon,
            IProjectArea projectArea,
            String enumId,
            String enumDisplayName,
            String[][] literals) throws TeamRepositoryException {
        
        IEnumeration<ILiteral> enumeration = createEnumeration(
            workItemCommon, projectArea, enumId, enumDisplayName, literals);
        
        saveEnumeration(workItemCommon, enumeration);
        
        return enumeration;
    }
    
    /**
     * Find an existing enumeration by identifier
     * 
     * @param workItemCommon The work item common service
     * @param projectArea The project area
     * @param enumId The enumeration identifier
     * @return The enumeration if found, null otherwise
     */
    public static IEnumeration<ILiteral> findEnumerationById(
            IWorkItemCommon workItemCommon,
            IProjectArea projectArea,
            String enumId) {
        
        logger.debug("Looking for enumeration with ID: {}", enumId);
        
        try {
            // Note: This is a simplified approach. In practice, you might need
            // to iterate through all enumerations to find by identifier
            // The exact implementation depends on your RTC version and setup
            
            // For now, return null to indicate not found
            // In a real implementation, you would search through the project area's enumerations
            return null;
            
        } catch (Exception e) {
            logger.warn("Error searching for enumeration {}: {}", enumId, e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if an enumeration exists by identifier
     * 
     * @param workItemCommon The work item common service
     * @param projectArea The project area
     * @param enumId The enumeration identifier
     * @return true if the enumeration exists, false otherwise
     */
    public static boolean enumerationExists(
            IWorkItemCommon workItemCommon,
            IProjectArea projectArea,
            String enumId) {
        
        return findEnumerationById(workItemCommon, projectArea, enumId) != null;
    }
    
    /**
     * Print enumeration details to log
     * 
     * @param enumeration The enumeration to print
     */
    public static void printEnumerationDetails(IEnumeration<ILiteral> enumeration) {
        if (enumeration == null) {
            logger.info("Enumeration is null");
            return;
        }
        
        logger.info("Enumeration Details:");
        logger.info("  ID: {}", enumeration.getIdentifier());
        logger.info("  Display Name: {}", enumeration.getDisplayName());
        logger.info("  Literals ({}):", enumeration.getEnumerationLiterals().size());
        
        for (ILiteral literal : enumeration.getEnumerationLiterals()) {
            logger.info("    - {} ({}): sequence={}", 
                       literal.getName(),
                       literal.getIdentifier2(),
                       literal.getSequenceValue());
        }
    }
}