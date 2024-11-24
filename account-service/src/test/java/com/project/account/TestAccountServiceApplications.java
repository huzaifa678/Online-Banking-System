package com.project.account;

import com.github.tomakehurst.wiremock.admin.model.ListStubMappingsResult;
import com.project.account.stubs.UserStub;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;



import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class TestAccountServiceApplications {

    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.3.0");

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void createSchemaAndTable() {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS test;");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test.accounts (" +
                "account_id VARCHAR(20) PRIMARY KEY, " +
                "user_email VARCHAR(255) NOT NULL, " +
                "account_type VARCHAR(50) NOT NULL, " +
                "balance DECIMAL(15, 2), " +
                "status VARCHAR(20), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);");
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mysqlContainer.start();
    }

    @Test
    public void testAddAccount_UserDoesNotExist() throws Exception {

        String accountJson = """
                {
                    "account_Id": "1",
                    "userEmail": "me22@gmail.com",
                    "accountType": "SAVINGS",
                    "balance": 1000.00,
                    "status": "ACTIVE"
                }
                """;

        UserStub.userStubCall("me22@gmail.com");

        ListStubMappingsResult mappings = listAllStubMappings();
        mappings.getMappings().forEach(System.out::println);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(accountJson)
                .log().all()
                .when()
                .post("/api/accounts/register")
                .then()
                .log().all()
                .statusCode(404);

    }


    @Test
    public void testAddAccount_UserDoesExist() throws Exception {

        String accountJson = """
                {
                    "accountId": "21",
                    "userEmail": "him25@example.com",
                    "accountType": "SAVINGS",
                    "balance": 1000.00,
                    "status": "ACTIVE"
                }
                """;
        UserStub.userStubCall("him25@example.com");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(accountJson)
                .log().all()
                .when()
                .post("/api/accounts/register")
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    public void testAddAccount_AccountStatusClosed() {
        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('2', '123@example.com', 'SAVINGS', 1000.00, 'CLOSED');");

        String accountJson = """
                {
                    "account_Id": "2",
                    "userEmail": "123@example.com"
                    "accountType": "SAVINGS",
                    "balance": 1000.00,
                    "status": "CLOSED"
                }
                """;

        UserStub.userStubCall("123@example.com");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(accountJson)
                .log().all()
                .when()
                .post("/api/accounts/register")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void testAddAccount_AccountStatusActive() {
        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('3', 'user@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        String accountJson = """
                {
                    "account_Id": 3,
                    "userEmail": "user@example.com",
                    "accountType": "SAVINGS",
                    "balance": 1000.00,
                    "status": "ACTIVE"
                }
                """;

        UserStub.userStubCall("user@example.com");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(accountJson)
                .log().all()
                .when()
                .post("/api/accounts/register")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void testCloseAccount_Success() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('4', 'user123@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        RestAssured.given()
                .when()
                .put("/api/accounts/close/4")
                .then()
                .statusCode(200)
                .log().all();

    }

    @Test
    public void testCloseAccount_AlreadyClosed() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('5', 'user12@example.com', 'SAVINGS', 1000.00, 'CLOSED');");


        RestAssured.given()
                .when()
                .put("/api/accounts/close/5")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void testUpdateStatus_Success() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('6', 'm909@gmail.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        String updateStatusJson = """
            {
                "status": "CLOSED"
            }
            """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateStatusJson)
                .log().all()
                .when()
                .put("/api/accounts/updateStatus/6")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    public void testUpdateStatus_AccountNotFound() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('7', 'm800@gmail.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        String updateStatusJson = """
            {
                "status": "CLOSED"
            }
            """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateStatusJson)
                .log().all()
                .when()
                .put("/api/accounts/updateStatus/20")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void testUpdateStatus_SameStatus() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('8', 'm8@gmail.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        String updateStatusJson = """
            {
                "status": "ACTIVE"
            }
            """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateStatusJson)
                .log().all()
                .when()
                .put("/api/accounts/updateStatus/8")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void testUpdateAccount_success() {
        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('9', 'this22@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        String updateAccountJson = """
                {
                    "accountId": "9",
                    "userEmail": "this22@example.com",
                    "accountType": "CHECKING",
                    "balance": 1000.75,
                    "status": "INACTIVE"
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateAccountJson)
                .log().all()
                .when()
                .put("/api/accounts/9")
                .then()
                .statusCode(200)
                .log().all();

    }

    @Test
    public void testUpdateAccount_AccountNotFound() {
        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('10', 'project123@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        String updateAccountJson = """
                {
                    "accountId": "10",
                    "user_id": "project123@example.com",
                    "accountType": "CHECKING",
                    "balance": 1000.75,
                    "status": "INACTIVE"
                }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updateAccountJson)
                .log().all()
                .when()
                .put("/api/accounts/20")
                .then()
                .statusCode(500)
                .log().all();

    }


    @Test
    public void testGetAccountById_Success() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('11', 'project128@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");


        RestAssured.given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/api/accounts/11")
                        .then()
                        .statusCode(200)
                        .log().all();
    }

    @Test
    public void testGetAccountById_AccountNotFound() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('12', 'project129@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");


        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/accounts/20")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void testDeleteAccount_Success() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('13', 'him22@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/accounts/13")
                .then()
                .statusCode(204)
                .log().all();
    }

    @Test
    public void testDeleteAccount_AccountNotFound() {

        jdbcTemplate.execute("INSERT INTO test.accounts (account_id, user_email, account_type, balance, status) " +
                "VALUES ('14', 'him23@example.com', 'SAVINGS', 1000.00, 'ACTIVE');");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/accounts/20")
                .then()
                .statusCode(500)
                .log().all();
    }
}
