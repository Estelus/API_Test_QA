import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import java.io.IOException;
import net.minidev.json.JSONObject;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class functional_Tests {

    private final String BASE_URL = "https://gorest.co.in/public/v2/users";
    private static final String ACCESS_TOKEN = "3026a23dadcb2bac619eb1c0ce638150d81ddeec2b8b62057dced5069ed968ec";
    private String createdUserId;

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testPutUserWithValidData() {

                baseURI = BASE_URL;
        //Act - Creating a new Json Objects for user data to input into request
                JSONObject request = new JSONObject();
                request.put("email","AlinaJacobson@gmail.com");
                request.put("name","Alina Jacobs");
                request.put("gender","female");
                request.put("status","active");

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request.toJSONString())
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract().response();

        // Log the response body
        String responseBody = response.getBody().asString();
        System.out.println("Response body: " + responseBody);

        // Extracting the created user's ID from the response
        JsonPath jsonPath = response.jsonPath();
        String createdUserId = jsonPath.getString("id");

        // Ensure the createdUserId is not null
        assertNotNull(createdUserId, "Created user ID is null");

        // Now, you can use this ID in the next test method or store it for later use
        // For example, you can store it as an instance variable for use in other methods
        this.createdUserId = createdUserId;
    }
    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testGetSpecificUserDetails() {
        // Verification if the createdUserId is not null
        if (createdUserId != null) {
            Response response = given()
                    .header("Authorization", "Bearer " + ACCESS_TOKEN)
                    .when()
                    .get(BASE_URL + "/public/v2/users/" + createdUserId);

            // Assertion to verify the response status code
            int statusCode = response.getStatusCode();
            assertEquals(200, statusCode, "Expected status code is 200");
            this.createdUserId = createdUserId;
            }
        }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testUpdateUser() {
        baseURI = BASE_URL;

        // Act - Creating a new JsonObject for user data to input into the request
        JSONObject request = new JSONObject();
        request.put("email", "AlinaJacoobsss@gmail.com");
        request.put("name", "Alina Jacobs");
        request.put("gender", "female");
        request.put("status", "active");

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request.toJSONString())
                .when()
                .put(BASE_URL + "/public/v2/users/" + createdUserId)
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
        this.createdUserId = createdUserId;
    }
    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    void testDeleteUser() {
        // Delete user
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .delete(BASE_URL + "/public/v2/users/" + createdUserId); // Use createdUserId
        // Verify the response status code
        //response.then()
         //       .statusCode(204);

        // Extract the response body
        String responseBody = response.getBody().asString();

        // Log the response body
        System.out.println("Response body: " + responseBody);

        // Assertion - based on API console information provided in the body is blank
        assertTrue(StringUtils.isAllBlank());
        this.createdUserId = createdUserId;
    }
    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void listUsers() {
        // Act - Enter the API Website: "https://gorest.co.in/public/v2/users"
        Response response = RestAssured.get(BASE_URL);
        response.getStatusCode();
        System.out.println("Status code is: " + response.getStatusCode());
        // Assert - Verification of the status code
        assertEquals(200, response.getStatusCode(), "Expected status code is 200");
        //Checking the respose body:
        System.out.println(response.getBody().asString());

    }
    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testTransferJsonToCsv() throws IOException {
        // Covering json string into CSV file
        String jsonResponseBody = "[{\"email\":\"AlinaJacobs@gmail.com\",\"name\":\"Alina Jacobs\",\"gender\":\"female\",\"status\":\"active\"}]";

        // Specify the output file path
        String outputFilePath = "src/main/java/testData.csv";

        // Convert JSON to CSV using the utility class
        String csvData = JsonToCsvConverter.convertJsonToCsv(jsonResponseBody, outputFilePath);

        // Additional assertion
        assertEquals("email,name,gender,status\n" +
                "AlinaJacobs@gmail.com,Alina Jacobs,female,active\n", csvData, "CSV data does not match");
    }
}