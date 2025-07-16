package com.example.servlet;

import com.example.util.ConfigurationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet responsible for handling role validation requests
 * and communicating with external REST API
 */
public class RoleValidationServlet extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleValidationServlet.class);
    private ObjectMapper objectMapper;
    private ConfigurationManager configManager;
    
    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = new ObjectMapper();
        configManager = ConfigurationManager.getInstance();
        logger.info("RoleValidationServlet initialized successfully");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Map<String, Object> responseData = new HashMap<>();
        
        try {
            // Get parameters from request
            String roleName = request.getParameter("roleName");
            String roleDescription = request.getParameter("roleDescription");
            
            logger.info("Received validation request for role: {}", roleName);
            
            // Validate input parameters
            if (roleName == null || roleName.trim().isEmpty() || 
                roleDescription == null || roleDescription.trim().isEmpty()) {
                
                responseData.put("success", false);
                responseData.put("message", "Role name and description are required");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(objectMapper.writeValueAsString(responseData));
                return;
            }
            
            // Call external REST API
            Map<String, Object> apiResponse = callExternalAPI(roleName.trim(), roleDescription.trim());
            
            if (apiResponse != null && apiResponse.containsKey("success")) {
                responseData.putAll(apiResponse);
            } else {
                responseData.put("success", true);
                responseData.put("message", "Role validation completed successfully");
                responseData.put("roleName", roleName.trim());
                responseData.put("roleDescription", roleDescription.trim());
                responseData.put("validationResult", "Role is valid and has been processed");
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            logger.error("Error processing role validation request", e);
            responseData.put("success", false);
            responseData.put("message", "Internal server error occurred during validation");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        out.print(objectMapper.writeValueAsString(responseData));
    }
    
    /**
     * Calls external REST API for role validation
     */
    private Map<String, Object> callExternalAPI(String roleName, String roleDescription) {
        CloseableHttpClient httpClient = null;
        Map<String, Object> result = new HashMap<>();
        
        try {
            httpClient = HttpClients.createDefault();
            String apiUrl = configManager.getRestApiUrl();
            HttpPost httpPost = new HttpPost(apiUrl);
            
            // Prepare request payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", roleName);
            payload.put("body", roleDescription);
            payload.put("userId", 1);
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            StringEntity entity = new StringEntity(jsonPayload);
            
            // Set headers
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            
            logger.info("Calling external API: {}", apiUrl);
            
            // Execute request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            
            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity);
                int statusCode = response.getStatusLine().getStatusCode();
                
                logger.info("API Response Status: {}", statusCode);
                logger.debug("API Response Body: {}", responseString);
                
                if (statusCode >= 200 && statusCode < 300) {
                    // Parse response
                    Map<String, Object> apiResult = objectMapper.readValue(responseString, Map.class);
                    
                    result.put("success", true);
                    result.put("message", "Role validation successful via external API");
                    result.put("apiResponse", apiResult);
                    result.put("roleName", roleName);
                    result.put("roleDescription", roleDescription);
                    result.put("validationTimestamp", System.currentTimeMillis());
                } else {
                    result.put("success", false);
                    result.put("message", "External API returned error: " + statusCode);
                }
            } else {
                result.put("success", false);
                result.put("message", "No response received from external API");
            }
            
        } catch (Exception e) {
            logger.error("Error calling external REST API", e);
            result.put("success", false);
            result.put("message", "Failed to communicate with external API: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.warn("Error closing HTTP client", e);
                }
            }
        }
        
        return result;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> statusResponse = new HashMap<>();
        statusResponse.put("status", "active");
        statusResponse.put("message", "Role Validation Service is running");
        statusResponse.put("timestamp", System.currentTimeMillis());
        
        PrintWriter out = response.getWriter();
        out.print(objectMapper.writeValueAsString(statusResponse));
    }
}