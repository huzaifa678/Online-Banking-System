package com.project.account;

import com.github.tomakehurst.wiremock.admin.model.ListStubMappingsResult;
import com.project.account.config.TestKafkaConfig;
import com.project.account.stubs.UserStub;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@Import(TestKafkaConfig.class)
@ActiveProfiles("test")
public class TestAccountServiceApplications {

    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.3.0");

    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"))
            .waitingFor(Wait.forLogMessage(".*started.*\\n", 1))
            .withEmbeddedZookeeper();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaConsumer<String, String> consumer;

    @BeforeAll
    static void beforeAll() {
        mysqlContainer.start();
        kafkaContainer.start();
    }

    @AfterAll
    static void afterAll() {
        if (mysqlContainer.isRunning()) {
            mysqlContainer.stop();
        }
        if (kafkaContainer.isRunning()) {
            kafkaContainer.stop();
        }
    }

    @BeforeEach
    public void createSchemaAndTable() {
        try {
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS test;");
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test.accounts (" +
                    "account_id VARCHAR(20) PRIMARY KEY, " +
                    "user_email VARCHAR(255) NOT NULL, " +
                    "account_type VARCHAR(50) NOT NULL, " +
                    "balance DECIMAL(15, 2), " +
                    "status VARCHAR(20), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);");
        } catch (Exception e) {
            System.err.println("Error creating schema/table: " + e.getMessage());
            throw e;
        }
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(props);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
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

        String accountJson = """
                {
                    "account_Id": 4,
                    "userEmail": "user123@example.com",
                    "accountType": "SAVINGS",
                    "balance": 1000.00,
                    "status": "ACTIVE"
                }
                """;

        // Subscribe to the topic before making the request
        consumer.subscribe(Collections.singletonList("account-closed"));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(accountJson)
                .log().all()
                .when()
                .put("/api/accounts/close/4")
                .then()
                .log().all()
                .statusCode(200);

        // Verify the message was sent to Kafka
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        assertNotNull(records, "No records received from Kafka");
        assertNotNull(records.records("account-closed"), "No records found in account-closed topic");
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
