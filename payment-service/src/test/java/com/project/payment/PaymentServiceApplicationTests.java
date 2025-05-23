package com.project.payment;

import com.project.payment.Stub.AccountStub;
import com.project.payment.client.kafkaTest;
import com.project.payment.event.PaymentCreatedEvent;
import com.project.payment.model.Status;
import com.project.payment.model.document.Payment;
import com.project.payment.model.paymentMethod;
import com.project.payment.repository.PaymentRepository;
import com.stripe.model.PaymentIntent;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"spring.profiles.active=test",
				"account.service.url=http://localhost:${wiremock.server.port}",
				"stripe.api.key=sk_test_51RProu4dWuzF8mxlOxWlK4YHCJ6M8LfNSSggvjdbxyDkzgL3AxSnQnHQTVy2JQUjHwo9Mj9SF63cuOj38SWJESrO00NXkoo5OY"
		}
)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@Import(kafkaTest.class)
class PaymentServiceApplicationTests {

	@LocalServerPort
	private int port;

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		mongoTemplate.dropCollection(Payment.class);
		AccountStub.reset();
	}

	@Container
	@ServiceConnection
	static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7.4.2"))
			.withExposedPorts(6379);

	static {
		mongoDBContainer.start();
		redis.start();
		System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getConnectionString());
		System.setProperty("redis.port", String.valueOf(redis.getMappedPort(6379)));
		System.out.println("MongoDB Container URI: " + mongoDBContainer.getConnectionString());
	}

	@Test
	void redisSerializationTest() {
		System.out.println("Redis container host: " + redis.getHost());
		System.out.println("Redis container mapped port: " + redis.getFirstMappedPort());

		Payment payment = new Payment();
		payment.setSource_accountId("1");
		payment.setDestination_accountId("2");
		payment.setAmount(new BigDecimal("100"));
		payment.setStatus(Status.COMPLETED);
		payment.setPaymentDate(LocalDateTime.now());

		System.out.println("Payment object to save: " + payment);
		System.out.println("Payment class implements Serializable: " + (payment instanceof java.io.Serializable));

		try {
			System.out.println("Key serializer: " + redisTemplate.getKeySerializer().getClass().getName());
			System.out.println("Value serializer: " + redisTemplate.getValueSerializer().getClass().getName());

			String key = "payment:" + System.currentTimeMillis();
			System.out.println("Saving payment with key: " + key);

			redisTemplate.opsForValue().set(key, payment);

			Object retrieved = redisTemplate.opsForValue().get(key);
			System.out.println("Retrieved payment from Redis: " + retrieved);

			assertNotNull(retrieved, "Retrieved payment should not be null");

		} catch (Exception e) {
			System.err.println("Exception during Redis operation: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	void createPayment_Success() {
		String sourceAccountId = "1";
		String destinationAccountId = "2";
		String paymentMethodId = "pm_card_visa";

		String paymentJson = """
            {
                "@type": "PaymentCardDto",
                "paymentDto": {
                    "@type": "PaymentDto",
                    "source_accountId": "1",
                    "destination_accountId": "2",
                    "amount": ["java.math.BigDecimal", "100.00"],
                    "paymentMethod": "CREDIT_CARD"
                },
                "paymentMethodId": "pm_card_visa"
            }
            """;

		AccountStub.stubDoesAccountExist(sourceAccountId, true);
		AccountStub.stubDoesAccountExist(destinationAccountId, true);
		AccountStub.stubIsAccountClosed(sourceAccountId, false);
		AccountStub.stubIsAccountClosed(destinationAccountId, false);
		AccountStub.stubAccountBalance(sourceAccountId, BigDecimal.valueOf(500), true);
		AccountStub.stubDebitAccountBalance(BigDecimal.valueOf(100), sourceAccountId, BigDecimal.valueOf(400));
		AccountStub.stubCreditAccountBalance(BigDecimal.valueOf(100), destinationAccountId, BigDecimal.valueOf(600));

		try (MockedStatic<PaymentIntent> mockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
			PaymentIntent mockPaymentIntent = Mockito.mock(PaymentIntent.class);
			Mockito.when(mockPaymentIntent.getStatus()).thenReturn("succeeded");
			mockedStatic.when(() -> PaymentIntent.create(Mockito.any(Map.class))).thenReturn(mockPaymentIntent);

			String response = RestAssured
					.given()
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(paymentJson)
					.post("/api/payment/create")
					.then()
					.statusCode(HttpStatus.OK.value())
					.extract()
					.asString();

			System.out.println("Response: " + response);
		}
	}

	@Test
	void createPayment_StripePaymentFailed() {
		String sourceAccountId = "1";
		String destinationAccountId = "2";
		String paymentMethodId = "pm_card_visa";

		String paymentJson = """
            {
                "@type": "PaymentCardDto",
                "paymentDto": {
                    "@type": "PaymentDto",
                    "source_accountId": "1",
                    "destination_accountId": "2",
                    "amount": 100.00
                },
                "paymentMethodId": "pm_card_visa"
            }
            """;

		AccountStub.stubDoesAccountExist(sourceAccountId, true);
		AccountStub.stubDoesAccountExist(destinationAccountId, true);
		AccountStub.stubIsAccountClosed(sourceAccountId, false);
		AccountStub.stubIsAccountClosed(destinationAccountId, false);
		AccountStub.stubAccountBalance(sourceAccountId, BigDecimal.valueOf(500), true);

		try (MockedStatic<PaymentIntent> mockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
			PaymentIntent mockPaymentIntent = Mockito.mock(PaymentIntent.class);
			Mockito.when(mockPaymentIntent.getStatus()).thenReturn("failed");
			mockedStatic.when(() -> PaymentIntent.create(Mockito.any(Map.class))).thenReturn(mockPaymentIntent);

			RestAssured
					.given()
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(paymentJson)
					.post("/api/payment/create")
					.then()
					.statusCode(HttpStatus.BAD_REQUEST.value());
		}
	}

	@Test
	void createPayment_StripeException() {
		String sourceAccountId = "1";
		String destinationAccountId = "2";
		String paymentMethodId = "pm_card_visa";

		String paymentJson = """
            {
                "@type": "PaymentCardDto",
                "paymentDto": {
                    "@type": "PaymentDto",
                    "source_accountId": "1",
                    "destination_accountId": "2",
                    "amount": 100.00
                },
                "paymentMethodId": "pm_card_visa"
            }
            """;

		AccountStub.stubDoesAccountExist(sourceAccountId, true);
		AccountStub.stubDoesAccountExist(destinationAccountId, true);
		AccountStub.stubIsAccountClosed(sourceAccountId, false);
		AccountStub.stubIsAccountClosed(destinationAccountId, false);
		AccountStub.stubAccountBalance(sourceAccountId, BigDecimal.valueOf(500), true);

		try (MockedStatic<PaymentIntent> mockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
			mockedStatic.when(() -> PaymentIntent.create(Mockito.any(Map.class)))
					.thenThrow(new RuntimeException("Stripe API error"));

			RestAssured
					.given()
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(paymentJson)
					.post("/api/payment/create")
					.then()
					.statusCode(HttpStatus.BAD_REQUEST.value());
		}
	}

	@Test
	void createPayment_InsufficientBalance() {
		String sourceAccountId = "1";
		String destinationAccountId = "2";
		String paymentMethodId = "pm_card_visa";

		String paymentJson = """
            {
                "@type": "PaymentCardDto",
                "paymentDto": {
                    "@type": "PaymentDto",
                    "source_accountId": "1",
                    "destination_accountId": "2",
                    "amount": 5000.00
                },
                "paymentMethodId": "pm_card_visa"
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
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void createPayment_SourceAccountClosed() {
		String sourceAccountId = "3";
		String destinationAccountId = "2";
		String paymentMethodId = "pm_card_visa";

		String paymentJson = """
            {
                "@type": "PaymentCardDto",
                "paymentDto": {
                    "@type": "PaymentDto",
                    "source_accountId": "3",
                    "destination_accountId": "2",
                    "amount": 5000.00
                },
                "paymentMethodId": "pm_card_visa"
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
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	void createPayment_DestinationAccountClosed() {
		String sourceAccountId = "2";
		String destinationAccountId = "3";
		String paymentMethodId = "pm_card_visa";

		String paymentJson = """
            {
                "@type": "PaymentCardDto",
                "paymentDto": {
                    "@type": "PaymentDto",
                    "source_accountId": "2",
                    "destination_accountId": "3",
                    "amount": 5000.00
                },
                "paymentMethodId": "pm_card_visa"
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
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}


	@Test
	void getPaymentById_Success() {
		mongoTemplate.dropCollection(Payment.class);
		System.out.println("Cleared payment collection");

		Payment payment = new Payment();
		payment.setSource_accountId("1");
		payment.setDestination_accountId("2");
		payment.setAmount(new BigDecimal("100"));
		payment.setStatus(Status.COMPLETED);
		payment.setPaymentDate(LocalDateTime.now());

		System.out.println("Saving payment to MongoDB...");
		Payment savedPayment = mongoTemplate.save(payment);
		String paymentId = savedPayment.getPaymentId();
		System.out.println("Saved payment with ID: " + paymentId);

		Payment foundPayment = mongoTemplate.findById(paymentId, Payment.class);
		System.out.println("Found payment in MongoDB: " + foundPayment);
		assertNotNull(foundPayment, "Payment should be found in MongoDB");

		Optional<Payment> persistedPayment = paymentRepository.findById(paymentId);
		System.out.println("Found payment through repository: " + persistedPayment);
		assertTrue(persistedPayment.isPresent(), "Payment should be found through repository");

		List<Payment> allPayments = mongoTemplate.findAll(Payment.class);
		System.out.println("All payments in MongoDB: " + allPayments);

		String requestUrl = "/api/payment/" + paymentId;
		System.out.println("Making request to: " + requestUrl);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get(requestUrl)
				.then()
				.log().all()
				.statusCode(HttpStatus.OK.value());
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
				.statusCode(HttpStatus.NOT_FOUND.value())
				.log().all();
	}
}
