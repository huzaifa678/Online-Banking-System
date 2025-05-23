package com.project.payment;

import com.project.payment.model.Status;
import com.project.payment.model.document.Payment;
import com.project.payment.model.paymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Container
    @ServiceConnection
    static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7.4.2"))
            .withExposedPorts(6379);

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDBContainer.start();
        redis.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getConnectionString());
        System.setProperty("redis.port", String.valueOf(redis.getMappedPort(6379)));
    }

    @BeforeEach
    void setup() {
        redisTemplate.delete(redisTemplate.keys("*"));
    }

    @Test
    void testPaymentSerialization() {
        Payment payment = new Payment();
        payment.setPaymentId("test-123");
        payment.setSource_accountId("acc-1");
        payment.setDestination_accountId("acc-2");
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus(Status.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentmethod(paymentMethod.CREDIT_CARD);

        String key = "payment:" + payment.getPaymentId();
        redisTemplate.opsForValue().set(key, payment);

        Payment retrieved = (Payment) redisTemplate.opsForValue().get(key);

        assertNotNull(retrieved);
        assertEquals(payment.getPaymentId(), retrieved.getPaymentId());
        assertEquals(payment.getSource_accountId(), retrieved.getSource_accountId());
        assertEquals(payment.getDestination_accountId(), retrieved.getDestination_accountId());
        assertEquals(payment.getAmount(), retrieved.getAmount());
        assertEquals(payment.getStatus(), retrieved.getStatus());
        assertEquals(payment.getPaymentmethod(), retrieved.getPaymentmethod());
    }

    @Test
    void testPaymentExpiration() {
        Payment payment = new Payment();
        payment.setPaymentId("exp-test-123");
        payment.setAmount(new BigDecimal("50.00"));

        String key = "payment:" + payment.getPaymentId();
        redisTemplate.opsForValue().set(key, payment, 5, TimeUnit.SECONDS);

        assertNotNull(redisTemplate.opsForValue().get(key));

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertNull(redisTemplate.opsForValue().get(key));
    }

    @Test
    void testPaymentListOperations() {
        Payment p1 = new Payment();
        p1.setPaymentId("list-1");
        p1.setAmount(new BigDecimal("100.00"));
        p1.setStatus(Status.COMPLETED);

        Payment p2 = new Payment();
        p2.setPaymentId("list-2");
        p2.setAmount(new BigDecimal("200.00"));
        p2.setStatus(Status.PENDING);

        String listKey = "payment:list";
        redisTemplate.opsForList().rightPushAll(listKey, p1, p2);

        Long size = redisTemplate.opsForList().size(listKey);
        assertEquals(2L, size);

        List<Object> payments = redisTemplate.opsForList().range(listKey, 0, -1);
        assertNotNull(payments);
        assertEquals(2, payments.size());

        Payment first = (Payment) payments.get(0);
        assertEquals("list-1", first.getPaymentId());
        assertEquals(new BigDecimal("100.00"), first.getAmount());
        assertEquals(Status.COMPLETED, first.getStatus());

        Payment second = (Payment) payments.get(1);
        assertEquals("list-2", second.getPaymentId());
        assertEquals(new BigDecimal("200.00"), second.getAmount());
        assertEquals(Status.PENDING, second.getStatus());
    }

    @Test
    void testPaymentHashOperations() {
        Payment payment = new Payment();
        payment.setPaymentId("hash-123");
        payment.setAmount(new BigDecimal("150.00"));
        payment.setStatus(Status.COMPLETED);
        payment.setPaymentmethod(paymentMethod.CREDIT_CARD);

        String hashKey = "payment:hash";
        redisTemplate.opsForHash().put(hashKey, payment.getPaymentId(), payment);

        Object retrieved = redisTemplate.opsForHash().get(hashKey, payment.getPaymentId());
        assertNotNull(retrieved);

        Payment retrievedPayment = (Payment) retrieved;
        assertEquals(payment.getPaymentId(), retrievedPayment.getPaymentId());
        assertEquals(payment.getAmount(), retrievedPayment.getAmount());
        assertEquals(payment.getStatus(), retrievedPayment.getStatus());
        assertEquals(payment.getPaymentmethod(), retrievedPayment.getPaymentmethod());
    }

    @Test
    void testPaymentCacheOperations() {
        Payment payment = new Payment();
        payment.setPaymentId("cache-123");
        payment.setAmount(new BigDecimal("75.00"));
        payment.setStatus(Status.COMPLETED);

        String cacheKey = "payments::" + payment.getPaymentId();
        redisTemplate.opsForValue().set(cacheKey, payment);

        Payment cached = (Payment) redisTemplate.opsForValue().get(cacheKey);
        assertNotNull(cached);
        assertEquals(payment.getPaymentId(), cached.getPaymentId());
        assertEquals(payment.getAmount(), cached.getAmount());
        assertEquals(payment.getStatus(), cached.getStatus());

        redisTemplate.delete(cacheKey);
        assertNull(redisTemplate.opsForValue().get(cacheKey));
    }
} 