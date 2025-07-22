import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.ILoginHandler;
import com.ibm.team.repository.client.login.ILoginInfo;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.client.WorkItemWorkingCopy;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.ICategory;
import com.ibm.team.workitem.common.model.ICategoryHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.common.IItemManager;
import com.ibm.team.repository.common.util.NLS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * IBM RTC Bulk Category Adder
 * 
 * This client program connects to IBM RTC and adds categories in bulk.
 * Categories can be read from a CSV file or defined programmatically.
 * 
 * Usage:
 * java RTCBulkCategoryAdder <server_url> <username> <password> <project_area> [categories_file]
 * 
 * CSV Format: categoryName,categoryDescription
 */
public class RTCBulkCategoryAdder {
    
    private static final String CATEGORIES_FILE_DEFAULT = "categories.csv";
    
    private ITeamRepository repository;
    private IWorkItemClient workItemClient;
    private IProjectArea projectArea;
    
    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: java RTCBulkCategoryAdder <server_url> <username> <password> <project_area> [categories_file]");
            System.err.println("Example: java RTCBulkCategoryAdder https://rtc.company.com:9443/ccm john.doe mypassword \"My Project Area\"");
            System.exit(1);
        }
        
        String serverUrl = args[0];
        String username = args[1];
        String password = args[2];
        String projectAreaName = args[3];
        String categoriesFile = args.length > 4 ? args[4] : CATEGORIES_FILE_DEFAULT;
        
        RTCBulkCategoryAdder adder = new RTCBulkCategoryAdder();
        
        try {
            System.out.println("Starting IBM RTC Bulk Category Adder...");
            
            // Initialize and login
            adder.initialize();
            adder.login(serverUrl, username, password);
            
            // Find project area
            adder.findProjectArea(projectAreaName);
            
            // Load categories from file or use default ones
            List<CategoryData> categories = adder.loadCategories(categoriesFile);
            
            // Add categories in bulk
            adder.addCategories(categories);
            
            System.out.println("Bulk category addition completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error during bulk category addition: " + e.getMessage());
            e.printStackTrace();
        } finally {
            adder.cleanup();
        }
    }
    
    /**
     * Initialize the Team Platform
     */
    private void initialize() {
        System.out.println("Initializing Team Platform...");
        TeamPlatform.startup();
    }
    
    /**
     * Login to the RTC repository
     */
    private void login(String serverUrl, String username, String password) throws TeamRepositoryException {
        System.out.println("Connecting to RTC server: " + serverUrl);
        
        repository = TeamPlatform.getTeamRepositoryService().getTeamRepository(URI.create(serverUrl));
        repository.registerLoginHandler(new ILoginHandler() {
            public ILoginInfo challenge(ITeamRepository repository) {
                return new ILoginInfo() {
                    public String getUserId() {
                        return username;
                    }
                    
                    public String getPassword() {
                        return password;
                    }
                };
            }
        });
        
        repository.login(null);
        workItemClient = (IWorkItemClient) repository.getClientLibrary(IWorkItemClient.class);
        
        System.out.println("Successfully logged in as: " + username);
    }
    
    /**
     * Find the project area by name
     */
    private void findProjectArea(String projectAreaName) throws TeamRepositoryException {
        System.out.println("Finding project area: " + projectAreaName);
        
        IProcessClientService processClient = (IProcessClientService) repository.getClientLibrary(IProcessClientService.class);
        URI uri = URI.create(projectAreaName.replaceAll(" ", "%20"));
        
        List<IProjectAreaHandle> projectAreaHandles = processClient.findAllProjectAreas(null);
        
        for (IProjectAreaHandle handle : projectAreaHandles) {
            IProjectArea pa = (IProjectArea) repository.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT, null);
            if (pa.getName().equals(projectAreaName)) {
                projectArea = pa;
                System.out.println("Found project area: " + projectArea.getName());
                return;
            }
        }
        
        throw new RuntimeException("Project area not found: " + projectAreaName);
    }
    
    /**
     * Load categories from CSV file or create default categories
     */
    private List<CategoryData> loadCategories(String categoriesFile) {
        List<CategoryData> categories = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(categoriesFile))) {
            System.out.println("Loading categories from file: " + categoriesFile);
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split(",", 2);
                if (parts.length >= 1) {
                    String name = parts[0].trim();
                    String description = parts.length > 1 ? parts[1].trim() : "";
                    categories.add(new CategoryData(name, description));
                    System.out.println("Loaded category: " + name);
                } else {
                    System.err.println("Skipping invalid line " + lineNumber + ": " + line);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Could not read categories file (" + categoriesFile + "), using default categories.");
            // Create some default categories
            categories.add(new CategoryData("Bug", "Software defects and issues"));
            categories.add(new CategoryData("Enhancement", "Feature requests and improvements"));
            categories.add(new CategoryData("Task", "General tasks and activities"));
            categories.add(new CategoryData("Story", "User stories and requirements"));
            categories.add(new CategoryData("Epic", "Large features or initiatives"));
        }
        
        System.out.println("Total categories to add: " + categories.size());
        return categories;
    }
    
    /**
     * Add categories in bulk to the project area
     */
    private void addCategories(List<CategoryData> categories) throws TeamRepositoryException {
        System.out.println("Starting bulk category addition...");
        
        IWorkItemCommon workItemCommon = workItemClient.getWorkItemCommon();
        
        // Get existing categories to avoid duplicates
        List<ICategory> existingCategories = workItemCommon.findCategories(projectArea, ICategory.FULL_PROFILE, null);
        List<String> existingCategoryNames = new ArrayList<>();
        
        for (ICategory category : existingCategories) {
            existingCategoryNames.add(category.getName());
        }
        
        int addedCount = 0;
        int skippedCount = 0;
        
        for (CategoryData categoryData : categories) {
            try {
                if (existingCategoryNames.contains(categoryData.getName())) {
                    System.out.println("Skipping existing category: " + categoryData.getName());
                    skippedCount++;
                    continue;
                }
                
                // Create new category
                ICategory category = workItemCommon.getWorkItemTypeManager().createCategory();
                category.setName(categoryData.getName());
                category.setHTMLSummary(categoryData.getDescription());
                
                // Save the category
                workItemCommon.saveCategory(category, projectArea, null);
                
                System.out.println("Added category: " + categoryData.getName());
                addedCount++;
                
                // Add a small delay to avoid overwhelming the server
                Thread.sleep(100);
                
            } catch (Exception e) {
                System.err.println("Failed to add category '" + categoryData.getName() + "': " + e.getMessage());
            }
        }
        
        System.out.println("Category addition summary:");
        System.out.println("  - Added: " + addedCount);
        System.out.println("  - Skipped (already exists): " + skippedCount);
        System.out.println("  - Total processed: " + categories.size());
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        if (repository != null) {
            try {
                repository.logout();
                System.out.println("Logged out from RTC server.");
            } catch (TeamRepositoryException e) {
                System.err.println("Error during logout: " + e.getMessage());
            }
        }
        TeamPlatform.shutdown();
        System.out.println("Team Platform shutdown completed.");
    }
    
    /**
     * Simple data class to hold category information
     */
    private static class CategoryData {
        private final String name;
        private final String description;
        
        public CategoryData(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
