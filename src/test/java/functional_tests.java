import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import java.io.IOException;
import net.minidev.json.JSONObject;
import static io.restassured.RestAssured.*;
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
    private static final String ACCESS_TOKEN = "3026a23dadcb2bac619eb1c0ce638150d81ddeec2b8b62057dced5069ed968ec";

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testPutUserWithValidData() {

        //Act - Creating a new Json Objects for user data to input into request
                JSONObject request = new JSONObject();
                request.put("email","AlinaJacobson@gmail.com");
                request.put("name","Alina Jacobs");
                request.put("gender","female");
                request.put("status","active");

        RestAssured.given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request.toJSONString())
                .when()
                .post(BASE_URL)
                .then()
        // Assert - Verification of the status code
                .statusCode(201);
    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testGetSpecificUserDetails() {


        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .get(BASE_URL + "/1700718");

        // Assertion to verify the response status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode, "Expected status code is 200");
    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ)
    void testUpdateUser() {

        //Test how to update user based on API Documentation

        RestAssured.baseURI = BASE_URL;

        //Act - Creating a new Json Objects for user data to input into request
        JSONObject request = new JSONObject();
        request.put("email","AlinaJacobss@gmail.com");
        request.put("name","Alina Jacobs");
        request.put("gender","female");
        request.put("status","active");

        RestAssured.given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request.toJSONString())
                .when()
                .patch(BASE_URL)
                .then()
                // Assert - Verification of the status code
                .statusCode(201);

    }

    @Test
    @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
    void testDeleteUser() {


        // Delete user
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .delete(BASE_URL + "/5711509"); //Endpoint for delete of the specific user

        // Verify the response status code
        response.then()
                .statusCode(204);
        // Extract the response body
        response.getBody().asString();

        // Assertion - based on API console information provided in the body is blank
        assertTrue(StringUtils.isAllBlank());
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
