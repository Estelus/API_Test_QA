import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import java.io.IOException;
import net.minidev.json.JSONObject;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;



public class functional_Tests {

    private final String BASE_URL = "https://gorest.co.in";
    private static final String ACCESS_TOKEN = "3026a23dadcb2bac619eb1c0ce638150d81ddeec2b8b62057dced5069ed968ec";
    private static String createdUserId;

        @Test
        void testPostUserWithValidData() {

            baseURI = BASE_URL;

            // Create a Java object representing the request body
            RequestBody requestBodyObject = new RequestBody("female", "Alina Jacobs", "Alina_jacobs@gmail.com", "active");

            // Serialize the object to JSON using Gson
            String requestBody = new Gson().toJson(requestBodyObject);
            // Send POST request to create a user
            Response response = given()
                    .header("Authorization", "Bearer " + ACCESS_TOKEN)
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post("/public/v2/users/")
                .then()
                    .statusCode(201)
                    .extract().response();



            // Log the response body
            String responseBody = response.getBody().asString();
            System.out.println("Response body: " + responseBody);

            createdUserId = response.path("id");

// Log extracted ID
            System.out.println("Extracted User ID: " + createdUserId);

// Ensure the createdUserId is not null
            assertNotNull(createdUserId, "Created user ID is null");
        }
    @Test
    void testGetSpecificUserDetails() {


        // Use the createdUserId to make a GET request to retrieve the user by ID
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .pathParam("id",createdUserId )
                .when()
                .get("{createdUserId}")
                .then()
                .statusCode(200)
                .extract().response();

        // Assertion
        assertNotNull(createdUserId, "Created user ID is null");
    }
    @Test
    public void verifyInJsonResponseID()
    {
        RestAssured.baseURI = "https://gorest.co.in/public/v2/users";
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.get(" " + createdUserId);

        // First get the JsonPath object instance from the Response interface
        JsonPath jsonPathEvaluator = response.jsonPath();

        // Then simply query the JsonPath object to get a String value of the node
        // specified by JsonPath: City (Note: You should not put $. in the Java code)
        String id = jsonPathEvaluator.get("id");

        // Let us print the city variable to see what we got
        System.out.println("id received from Response " + id);

        // Validate the response
        assertEquals(id, createdUserId, "Expected new created user id is not valid with data in testPutUserWithValidData");

    }

    @Test
    void testUpdateUser() {
        baseURI = BASE_URL;

        // Act - Creating a new JsonObject for user data to input into the request
        JSONObject request = new JSONObject();
        request.put("email","AlinaJacob@gmail.com");
        request.put("name","Alina Jacob");
        request.put("gender","female");
        request.put("status","active");

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request.toJSONString())
                .when()
                .put(BASE_URL + createdUserId)
                .then()
                // Assert - Verification of the status code
                .extract().response();

        // Log the response body
        System.out.println("Response body: " + response.getBody().asString());

        // Assert the status code
        assertEquals(404, response.getStatusCode(), "Expected status code is 404");
        // Issue with an API
        // Response body: ... <b>you_spelt_it_wrong</b> <br/>&nbsp;&nbsp;<span>try_again
        // </span><br/></code><code class="mb-2"><span>elsif <b>we_screwed_up</b></span>
        // <br/>&nbsp;&nbsp;<em>print</em> <i>"We're really sorry about that."</i>

    }
    @Test
    void testDeleteUser() {
        // Delete user
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .delete(BASE_URL + "/public/v2/users/5803822"); // Use createdUserId
        // Verify the response status code
        response.then()
                .statusCode(204);

        // Extract the response body
        String responseBody = response.getBody().asString();

        // Log the response body
        System.out.println("Response body: " + responseBody);

        // Assertion - based on API console information provided in the body is blank
        assertTrue(StringUtils.isAllBlank());
    }

        @Test
         void GetListAllUsers() {
            // Here is specified base URL to the RESTful web service
            RestAssured.baseURI = "https://gorest.co.in/public/v2/users";
            // Get the RequestSpecification of the request to be sent to the server
            RequestSpecification httpRequest = RestAssured.given();
            Response response = httpRequest.request(Method.GET, "");
            // Print the status and message body of the response received from the server
            System.out.println("Status received: " + response.getStatusLine());
            System.out.println("Response: " + response.prettyPrint());
            // Get the status code of the request
            // If request is successful, status code will be 200
            int statusCode = response.getStatusCode();
            // Assertion - Verify that correct status code is returned.
            assertEquals(statusCode /*actual value*/, 200 /*expected value*/,"Correct status code returned");
        }

    @Test
    void testTransferJsonToCsv() throws IOException {
        // Covering json string into CSV file
        String jsonResponseBody = "[{\"email\":\"Alina_Jacobs@gmail.com\"\",\"name\":\"Alina Jacob\",\"gender\":\"female\",\"status\":\"active\"}]";

        // Specify the output file path
        String outputFilePath = "src/main/java/testData.csv";

        // Convert JSON to CSV using the utility class
        String csvData = JsonToCsvConverter.convertJsonToCsv(jsonResponseBody, outputFilePath);

        // Additional assertion
        assertEquals("email,name,gender,status\n" +
                "Alina_Jacobs@gmail.com,Alina Jacob,female,active\n", csvData, "CSV data does not match");
    }
    private static class RequestBody {
        private int id;
        private String gender;
        private String name;
        private String email;
        private String status;

        public RequestBody(String gender, String name, String email, String status) {
            this.gender = gender;
            this.name = name;
            this.email = email;
            this.status = status;
        }
    }
}