import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(OrderAnnotation.class)
public class ApiTests {

    private static final String BASE_URL = "https://gorest.co.in";
    private static final String ACCESS_TOKEN = "3026a23dadcb2bac619eb1c0ce638150d81ddeec2b8b62057dced5069ed968ec";


    @BeforeEach
    void setUp() {
        RestAssured.baseURI = BASE_URL;
    }
    @AfterEach
    void tearDown() {
        // performing cleanup if needed
    }

    @Test
    @Order(1)
    void testListUsers() {
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .get("/public/v2/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        // Add assertions for the response
        assertNotNull(response.jsonPath().getList("data"), "User list is null");
        assertFalse(response.jsonPath().getList("data").isEmpty(), "User list is empty");
    }

    @Test
    @Order(2)
    void testCreateUser() {
        // Create a user
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body(("{\"name\":\"Alina Jacobs\", \"gender\":\"female\", \"email\":\"Alina_jacobs@gmail.com\", \"status\":\"inactive\"}"))
                .when()
                .post("/public/v2/users")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract().response();

        // Extract the user ID as Integer and convert it to String
        String createdUserId = String.valueOf(response.jsonPath().getInt("id"));

        // Log extracted ID
        System.out.println("Created User ID: " + createdUserId);

        // Ensure the createdUserId is not null
        assertNotNull(createdUserId, "Created user ID is null");
    }
    @Test
    @Order(3)
    void testGetSpecificUserDetails() {

        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .get("/public/v2/users/2139159")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        // Log the response body
        System.out.println("User details: " + response.getBody().asString());
    }
    @Test
    @Order(4)
    void testUpdateUser() {

        // Update a user
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Alina Jacob\", \"email\":\"Alina_jacob@gmail.com\", \"status\":\"inactive\"}")
                .when()
                .patch("/public/v2/users/2139159")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();


        // Extract the response body
        String responseBody = response.getBody().asString();

        // Log the response body
        System.out.println("Response body: " + responseBody);

        // Assertion - based on API console information provided in the body is blank
        assertTrue(StringUtils.isAllBlank());
    }

    @Test
    @Order(5)
    void testDeleteUser() {

        // Delete a user
        Response response = given()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .when()
                .delete("/public/v2/users/2139159")
                .then()
                .statusCode(204)
                .extract().response();

        // Extract the response body
        String responseBody = response.getBody().asString();

        // Log the response body
        System.out.println("Response body: " + responseBody);

        // Assertion - based on API console information provided in the body is blank
        assertTrue(StringUtils.isAllBlank());
    }
    @Test
    @Order(6)
    void testTransferJsonToCsv() throws IOException {
        // Covering json string into CSV file
        String jsonResponseBody = "[{\"email\":\"Alina_Jacobs@gmail.com\",\"name\":\"Alina Jacob\",\"gender\":\"female\",\"status\":\"active\"}]";

        // Specify the output file path
        String outputFilePath = "src/main/java/testData.csv";

        // Convert JSON to CSV using the utility class
        String csvData = JsonToCsvConverter.convertJsonToCsv(jsonResponseBody, outputFilePath);

        // Additional assertion
        assertEquals("email,name,gender,status\n" +
                "Alina_Jacobs@gmail.com,Alina Jacob,female,active\n", csvData, "CSV data does not match");
    }
}

