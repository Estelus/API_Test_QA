import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import java.io.IOException;
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
public class functional_tests {

    private final String BASE_URL = "https://gorest.co.in/public/v2/users";
    private final String ACCESS_TOKEN = "3026a23dadcb2bac619eb1c0ce638150d81ddeec2b8b62057dced5069ed968ec";


    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testCreateUserWithValidData() {
        String jsonRequestBody = "{ \"email\" : \"AlinaJacobs@gmail.com\", \"name\" : \"Alina Jacobs\", \"gender\" : \"female\", \"status\" : \"active\" }\n";

        // Create user
        RestAssured.given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(jsonRequestBody)
                .when()
                .post(BASE_URL)
        // Assertion - Verification of status code
                .then()
                .statusCode(201);

    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testGetSpecificUserDetails() {


        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .get(BASE_URL + "1700710");

        // Assertion to verify the response status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode, "Expected status code is 200");
    }


    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testUpdateUser() {

        //Test how to update user based on API Documentation

        RestAssured.baseURI = BASE_URL;

        //This user ID and request body are present in APi documentation

        String userId = "1700710";
        String requestBody = "{\"name\":\"Allasani Peddana\", \"email\":\"allasani.peddana@15ce.com\", \"status\":\"active\"}";

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/" + userId);

        response.then()
                .statusCode(200);

        // Performing additional assertion based on response body

        String updatedName = response.jsonPath().getString("data.name");
        assertEquals("Allasani Peddana", updatedName, "User name should be updated");


    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    void testDeleteUser() {


        // Delete user
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .delete(BASE_URL + "/1700710"); //Endpoint for delete of the user

        // Verify the response status code
        response.then()
                .statusCode(204);

        // Extract the response body
        String responseBody = response.getBody().asString();


        // Assertion - based on API console information provided in the body is blank
        assertTrue(StringUtils.isAllBlank());
    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void listUsers() {
        RestAssured.baseURI = BASE_URL;

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .accept("application/json")
                .contentType("application/json")
                .when()
                .get();

        response.then()
                .statusCode(200);
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
