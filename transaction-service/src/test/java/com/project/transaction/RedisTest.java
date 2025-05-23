package com.project.transaction;

import com.project.transaction.model.Status;
import com.project.transaction.model.TransactionTypes;
import com.project.transaction.model.document.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
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

    @BeforeEach
    void setup() {
        redisTemplate.delete(redisTemplate.keys("*"));
    }

    @Test
    void testTransactionSerialization() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("test-123");
        transaction.setSource_accountId("acc-1");
        transaction.setDestination_accountId("acc-2");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTransactionType(TransactionTypes.TRANSFER);
        transaction.setTransactionStatus(Status.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());

        String key = "transaction:" + transaction.getTransactionId();
        redisTemplate.opsForValue().set(key, transaction);

        Transaction retrieved = (Transaction) redisTemplate.opsForValue().get(key);

        assertNotNull(retrieved);
        assertEquals(transaction.getTransactionId(), retrieved.getTransactionId());
        assertEquals(transaction.getSource_accountId(), retrieved.getSource_accountId());
        assertEquals(transaction.getDestination_accountId(), retrieved.getDestination_accountId());
        assertEquals(transaction.getAmount(), retrieved.getAmount());
        assertEquals(transaction.getTransactionType(), retrieved.getTransactionType());
        assertEquals(transaction.getTransactionStatus(), retrieved.getTransactionStatus());
    }

    @Test
    void testTransactionExpiration() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("exp-test-123");
        transaction.setAmount(new BigDecimal("50.00"));

        String key = "transaction:" + transaction.getTransactionId();
        redisTemplate.opsForValue().set(key, transaction, 5, TimeUnit.SECONDS);

        assertNotNull(redisTemplate.opsForValue().get(key));

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertNull(redisTemplate.opsForValue().get(key));
    }

    @Test
    void testTransactionListOperations() {
        Transaction t1 = new Transaction();
        t1.setTransactionId("list-1");
        t1.setAmount(new BigDecimal("100.00"));

        Transaction t2 = new Transaction();
        t2.setTransactionId("list-2");
        t2.setAmount(new BigDecimal("200.00"));

        String listKey = "transaction:list";
        redisTemplate.opsForList().rightPushAll(listKey, t1, t2);

        Long size = redisTemplate.opsForList().size(listKey);
        assertEquals(2L, size);

        List<Object> transactions = redisTemplate.opsForList().range(listKey, 0, -1);
        assertNotNull(transactions);
        assertEquals(2, transactions.size());

        Transaction first = (Transaction) transactions.get(0);
        assertEquals("list-1", first.getTransactionId());
        assertEquals(new BigDecimal("100.00"), first.getAmount());

        Transaction second = (Transaction) transactions.get(1);
        assertEquals("list-2", second.getTransactionId());
        assertEquals(new BigDecimal("200.00"), second.getAmount());
    }

    @Test
    void testTransactionHashOperations() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("hash-123");
        transaction.setAmount(new BigDecimal("150.00"));

        String hashKey = "transaction:hash";
        redisTemplate.opsForHash().put(hashKey, transaction.getTransactionId(), transaction);

        Object retrieved = redisTemplate.opsForHash().get(hashKey, transaction.getTransactionId());
        assertNotNull(retrieved);

        Transaction retrievedTransaction = (Transaction) retrieved;
        assertEquals(transaction.getTransactionId(), retrievedTransaction.getTransactionId());
        assertEquals(transaction.getAmount(), retrievedTransaction.getAmount());
    }
} 