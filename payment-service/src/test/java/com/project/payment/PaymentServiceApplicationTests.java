package com.project.payment;

import com.project.payment.Stub.AccountStub;
import com.project.payment.model.document.Payment;
import com.project.payment.model.dto.CardDto;
import com.project.payment.model.dto.PaymentCardDto;
import com.project.payment.model.dto.PaymentDto;
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
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testcontainers.containers.MongoDBContainer;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class PaymentServiceApplicationTests {

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
		mongoTemplate.dropCollection(Payment.class);
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void createPayment_Success() {

		String sourceAccountId = "1";
		String destinationAccountId = "2";

		String paymentJson = """
				{
				    "paymentDto": {
				        "source_accountId": "1",
				        "destination_accountId": "2",
				        "amount": 100.00
				    },
				    "cardDto": {
				        "cardNumber": "4242-4242-4242-4242",
				        "expiryMonth": 12,
				        "expiryYear": 2024,
				        "cvc": "123"
				    }
				}
				""";

		AccountStub.stubDoesAccountExist(sourceAccountId, true);
		AccountStub.stubDoesAccountExist(destinationAccountId, true);
		AccountStub.stubIsAccountClosed(sourceAccountId, false);
		AccountStub.stubIsAccountClosed(destinationAccountId, false);
		AccountStub.stubAccountBalance(sourceAccountId, BigDecimal.valueOf(500), true);
		AccountStub.stubCreditAccountBalance(BigDecimal.valueOf(100), sourceAccountId, BigDecimal.valueOf(100));
		AccountStub.stubDebitAccountBalance(BigDecimal.valueOf(100), destinationAccountId, BigDecimal.valueOf(600));

		RestAssured
				.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(paymentJson)
				.post("/api/payment/create")
				.then()
				.statusCode(HttpStatus.CREATED.value());
	}

	@Test
	void createPayment_InsufficientBalance() {
		String sourceAccountId = "1";
		String destinationAccountId = "2";

		String paymentJson = """
				{
				    "paymentDto": {
				        "source_accountId": "1",
				        "destination_accountId": "2",
				        "amount": 5000.00
				    },
				    "cardDto": {
				        "cardNumber": "4242-4242-4242-4242",
				        "expiryMonth": 12,
				        "expiryYear": 2024,
				        "cvc": "123"
				    }
				}
				""";

		AccountStub.stubDoesAccountExist(sourceAccountId, true);
		AccountStub.stubDoesAccountExist(destinationAccountId, true);
		AccountStub.stubIsAccountClosed(sourceAccountId, false);
		AccountStub.stubIsAccountClosed(destinationAccountId, false);
		AccountStub.stubAccountBalance(sourceAccountId, BigDecimal.valueOf(5000), false);

		RestAssured
				.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(paymentJson)
				.post("/api/payment/create")
				.then()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@Test
	void createPayment_SourceAccountClosed() {

		String sourceAccountId = "3";
		String destinationAccountId = "2";

		String paymentJson = """
				{
				    "paymentDto": {
				        "source_accountId": "3",
				        "destination_accountId": "2",
				        "amount": 5000.00
				    },
				    "cardDto": {
				        "cardNumber": "4242-4242-4242-4242",
				        "expiryMonth": 12,
				        "expiryYear": 2024,
				        "cvc": "123"
				    }
				}
				""";

		AccountStub.stubDoesAccountExist(sourceAccountId, true);
		AccountStub.stubDoesAccountExist(destinationAccountId, true);
		AccountStub.stubIsAccountClosed(sourceAccountId, true);
		AccountStub.stubIsAccountClosed(destinationAccountId, false);

		RestAssured
				.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(paymentJson)
				.post("/api/payment/create")
				.then()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

	}

	@Test
	void createPayment_DestinationAccountClosed() {

		String sourceAccountId = "2";
		String destinationAccountId = "3";

		String paymentJson = """
				{
				    "paymentDto": {
				        "source_accountId": "2",
				        "destination_accountId": "3",
				        "amount": 5000.00
				    },
				    "cardDto": {
				        "cardNumber": "4242-4242-4242-4242",
				        "expiryMonth": 12,
				        "expiryYear": 2024,
				        "cvc": "123"
				    }
				}
				""";

		AccountStub.stubDoesAccountExist(sourceAccountId, true);
		AccountStub.stubDoesAccountExist(destinationAccountId, true);
		AccountStub.stubIsAccountClosed(sourceAccountId, false);
		AccountStub.stubIsAccountClosed(destinationAccountId, true);

		RestAssured
				.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(paymentJson)
				.post("/api/payment/create")
				.then()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

	}

	@Test
	void getPaymentById_Success() {
		String paymentId = "1";

		Payment payment = new Payment();
		payment.setPaymentId(paymentId);
		payment.setSource_accountId("1");
		payment.setDestination_accountId("2");
		payment.setAmount(new BigDecimal("100"));

		mongoTemplate.save(payment);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/payment/" + paymentId)
				.then()
				.statusCode(HttpStatus.OK.value())
				.log().all();
	}

	@Test
	void getPaymentById_PaymentNotFound() {
		String paymentId = "2";

		Payment payment = new Payment();
		payment.setPaymentId("1");
		payment.setSource_accountId("1");
		payment.setDestination_accountId("2");
		payment.setAmount(new BigDecimal("100"));

		mongoTemplate.save(payment);

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/payment/" + paymentId)
				.then()
				.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.log().all();
	}


}
