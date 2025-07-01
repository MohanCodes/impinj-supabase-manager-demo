package mohan.impinj;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SupabaseHttpUpdater {

    // --- CONFIGURATION - Loaded from config.properties ---
    private static String SUPABASE_URL;
    private static String SUPABASE_KEY;

    public static void main(String[] args) {
        // Step 1: Load configuration securely from the properties file
        if (!loadConfiguration()) {
            // Stop the application if configuration fails
            return;
        }

        // Step 2: Set up a main loop for the user menu
        // try-with-resources ensures the HttpClient and Scanner are always closed
        try (CloseableHttpClient client = HttpClients.createDefault(); 
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                printMenu();
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the rest of the line after reading the integer

                switch (choice) {
                    case 1:
                        createRecord(client, scanner);
                        break;
                    case 2:
                        readRecords(client);
                        break;
                    case 3:
                        updateRecord(client, scanner);
                        break;
                    case 4:
                        deleteRecord(client, scanner);
                        break;
                    case 5:
                        System.out.println("Exiting application.");
                        return; // Exit the main method and the program
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
                System.out.println("\n----------------------------------------\n");
            }
        } catch (Exception e) {
            System.err.println("A critical application error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean loadConfiguration() {
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            // Robustly load properties: trim whitespace and remove quotes
            SUPABASE_URL = prop.getProperty("SUPABASE_URL", "").trim().replace("\"", "");
            SUPABASE_KEY = prop.getProperty("SUPABASE_KEY", "").trim().replace("\"", "");

            if (SUPABASE_URL.isEmpty() || SUPABASE_KEY.isEmpty()) {
                System.err.println("Error: SUPABASE_URL or SUPABASE_KEY is missing from config.properties.");
                return false;
            }
            return true;
        } catch (Exception ex) {
            System.err.println("Error: Could not load config.properties file. Make sure it exists in the project root.");
            ex.printStackTrace();
            return false;
        }
    }

    private static void printMenu() {
        System.out.println("--- Supabase CRUD Manager ---");
        System.out.println("1. Create (Add) a new record");
        System.out.println("2. Read all records");
        System.out.println("3. Update an existing record");
        System.out.println("4. Delete a record");
        System.out.println("5. Exit");
    }

    // CREATE (POST)
    private static void createRecord(CloseableHttpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter the text for the new record: ");
        String newText = scanner.nextLine();
        
        String jsonBody = "{\"test\": \"" + newText + "\"}";
        HttpPost request = new HttpPost(SUPABASE_URL + "/rest/v1/test");
        
        // This header tells Supabase to return the newly created record in the response
        request.setHeader("Prefer", "return=representation");
        setCommonHeaders(request);
        request.setEntity(new StringEntity(jsonBody));
        
        System.out.println("\nSending CREATE request...");
        executeRequest(client, request);
    }

    // READ (GET)
    private static void readRecords(CloseableHttpClient client) throws Exception {
        HttpGet request = new HttpGet(SUPABASE_URL + "/rest/v1/test?select=*");
        setCommonHeaders(request);
        
        System.out.println("\nSending READ request...");
        executeRequest(client, request);
    }
    
    // UPDATE (PATCH)
    private static void updateRecord(CloseableHttpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter the ID of the record to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Enter the new text: ");
        String newText = scanner.nextLine();
        
        String jsonBody = "{\"test\": \"" + newText + "\"}";
        HttpPatch request = new HttpPatch(SUPABASE_URL + "/rest/v1/test?id=eq." + id);
        setCommonHeaders(request);
        request.setEntity(new StringEntity(jsonBody));
        
        System.out.println("\nSending UPDATE request...");
        executeRequest(client, request);
    }
    
    // DELETE (DELETE)
    private static void deleteRecord(CloseableHttpClient client, Scanner scanner) throws Exception {
        System.out.print("Enter the ID of the record to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        HttpDelete request = new HttpDelete(SUPABASE_URL + "/rest/v1/test?id=eq." + id);
        setCommonHeaders(request);
        
        System.out.println("\nSending DELETE request...");
        executeRequest(client, request);
    }

    // Helper method to set headers common to all requests
    private static void setCommonHeaders(HttpRequestBase request) {
        request.setHeader("Content-Type", "application/json");
        request.setHeader("apikey", SUPABASE_KEY);
        request.setHeader("Authorization", "Bearer " + SUPABASE_KEY);
    }

    // Helper method to execute requests and handle responses robustly
    private static void executeRequest(CloseableHttpClient client, HttpUriRequest request) throws Exception {
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            
            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("✅ Success! Status code: " + statusCode);
                // A successful response might still have a body (e.g., GET or CREATE)
                if (entity != null) {
                    System.out.println("Response Body:\n" + EntityUtils.toString(entity));
                }
            } else {
                System.err.println("❌ Error! Status code: " + statusCode);
                // An error response almost always has a body explaining the error
                if (entity != null) {
                    System.err.println("Response Body:\n" + EntityUtils.toString(entity));
                }
            }
        }
    }
}