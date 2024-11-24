package com.project.transaction;

import com.project.transaction.model.Status;
import com.project.transaction.model.TransactionTypes;
import com.project.transaction.model.document.Transaction;
import com.project.transaction.stub.AccountStub;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;

import java.math.BigDecimal;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class TransactionServiceApplicationTests {

	@LocalServerPort
	private int port;

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@Autowired
	private MongoTemplate mongoTemplate;


	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		mongoTemplate.dropCollection(Transaction.class);
	}

	static {
		mongoDBContainer.start();
	}


	@Test
	void testCreateTransaction_Deposit_Success() {

		String transactionJson = """
				{
				  "destination_accountId": "8",
				  "amount": 100.00,
				  "transactionType": "DEPOSIT"
				}
				""";


		AccountStub.stubDoesAccountExist("8", true);
		AccountStub.stubIsAccountClosed("8", false);
		AccountStub.stubCreditAccountBalance(BigDecimal.valueOf(100.00), "8", BigDecimal.valueOf(900.00));

		RestAssured
				.given()
				.contentType(ContentType.JSON)
				.body(transactionJson)
				.when()
				.post("/api/transaction/create")
				.then()
				.statusCode(HttpStatus.CREATED.value());
	}

	@Test
	void testCreateTransaction_Transfer_Success() {

		String transactionJson = """
				{
				  "source_accountId": "7",
				  "destination_accountId": "8",
				  "amount": 100.00,
				  "transactionType": "TRANSFER"
				}
				""";


		AccountStub.stubDoesAccountExist("7", true);
		AccountStub.stubAccountBalance("7", BigDecimal.valueOf(100.00), true);
		AccountStub.stubIsAccountClosed("7", false);

		AccountStub.stubDoesAccountExist("8", true);
		AccountStub.stubIsAccountClosed("8", false);
		AccountStub.stubCreditAccountBalance(BigDecimal.valueOf(100.00), "8", BigDecimal.valueOf(900.00));
		AccountStub.stubDebitAccountBalance(BigDecimal.valueOf(100.00), "7", BigDecimal.valueOf(1100.00));

		RestAssured
				.given()
				.contentType(ContentType.JSON)
				.body(transactionJson)
				.when()
				.post("/api/transaction/create")
				.then()
				.statusCode(HttpStatus.CREATED.value());
	}

	@Test
	void testCreateTransaction_Transfer_InsufficientFunds() {

		String transactionJson = """
			{
			  "source_accountId": "9",
			  "destination_accountId": "10",
			  "amount": 5000.00,
			  "transactionType": "TRANSFER"
			}
			""";

		AccountStub.stubDoesAccountExist("9", true);
		AccountStub.stubAccountBalance("9", BigDecimal.valueOf(500.00), false);
		AccountStub.stubIsAccountClosed("9", false);

		AccountStub.stubDoesAccountExist("10", true);
		AccountStub.stubIsAccountClosed("10", false);

		RestAssured
				.given()
				.contentType(ContentType.JSON)
				.body(transactionJson)
				.when()
				.post("/api/transaction/create")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void testCreateTransaction_Withdrawal_AccountClosed() {

		String transactionJson = """
			{
			  "source_accountId": 3,
			  "amount": 100.00,
			  "transactionType": "WITHDRAWAL"
			}
			""";

		AccountStub.stubDoesAccountExist("7", true);
		AccountStub.stubAccountBalance("7", BigDecimal.valueOf(100.00), true);
		AccountStub.stubIsAccountClosed("7", true);

		RestAssured
				.given()
				.contentType(ContentType.JSON)
				.body(transactionJson)
				.when()
				.post("/api/transaction/create")
				.then()
				.statusCode(HttpStatus.CONFLICT.value());
	}

	@Test
	void testCreateTransaction_Transfer_SourceAccountNotFound() {

		String transactionJson = """
			{
			  "source_accountId": "999",
			  "destination_accountId": "8",
			  "amount": 100.00,
			  "transactionType": "TRANSFER"
			}
			""";

		AccountStub.stubDoesAccountExist("999", false);
		AccountStub.stubDoesAccountExist("8", true);
		AccountStub.stubIsAccountClosed("8", false);

		RestAssured
				.given()
				.contentType(ContentType.JSON)
				.body(transactionJson)
				.when()
				.post("/api/transaction/create")
				.then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void testGetTransactionById_Success() {

		Transaction testTransaction = new Transaction();
		testTransaction.setTransactionId("12345abc");
		testTransaction.setSource_accountId("6");
		testTransaction.setDestination_accountId("7");
		testTransaction.setAmount(BigDecimal.valueOf(100.00));
		testTransaction.setTransactionStatus(Status.COMPLETED);
		mongoTemplate.insert(testTransaction);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/transaction/12345abc")
				.then()
				.statusCode(HttpStatus.OK.value())
				.log().all();
	}

	@Test
	void testGetTransactionById_TransactionIdNotFound() {

		Transaction testTransaction = new Transaction();
		testTransaction.setTransactionId("12345abc");
		testTransaction.setSource_accountId("6");
		testTransaction.setDestination_accountId("7");
		testTransaction.setAmount(BigDecimal.valueOf(100.00));
		testTransaction.setTransactionStatus(Status.COMPLETED);
		mongoTemplate.insert(testTransaction);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/transaction/1234abc")
				.then()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.log().all();
	}


}
