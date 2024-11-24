package com.project.payment.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import groovy.util.logging.Slf4j;

import java.math.BigDecimal;

@Slf4j
public interface AccountClient {

    Logger log = LoggerFactory.getLogger(AccountClient.class);

    @PostExchange("/api/accounts")
    @CircuitBreaker(name = "accountBalance", fallbackMethod = "fallbackForAccountBalance")
    @Retry(name = "accountBalance")
    boolean accountBalance(@RequestParam String id, @RequestParam BigDecimal transaction);

    @CircuitBreaker(name = "accountExists", fallbackMethod = "fallbackForDoesAccountExists")
    @Retry(name = "accountExists")
    @GetExchange("/api/accounts")
    boolean doesAccountExists(@RequestParam String id);

    @CircuitBreaker(name = "accountCredit", fallbackMethod = "fallbackForCreditAccountBalance")
    @Retry(name = "accountCredit")
    @PostExchange("/api/accounts/credit")
    BigDecimal creditAccountBalance(@RequestParam BigDecimal amount, @RequestParam String id);

    @CircuitBreaker(name = "accountDebit", fallbackMethod = "fallbackForDebitAccountBalance")
    @Retry(name = "accountDebit")
    @PostExchange("/api/accounts/debit")
    BigDecimal debitAccountBalance(@RequestParam BigDecimal amount, @RequestParam String id);

    @CircuitBreaker(name = "accountClosed", fallbackMethod = "fallbackForIsAccountClosed")
    @Retry(name = "accountClosed")
    @PostExchange("/api/accounts/isclosed")
    Boolean isAccountClosed(@RequestParam String id);


    default boolean fallbackForDoesAccountExists(String id, Throwable throwable) {
        log.error("Fallback for account existence: {}", throwable.getMessage());
        return false;
    }

    default boolean fallbackForAccountBalance(String id, BigDecimal transaction, Throwable throwable) {
        log.error("Fallback for account balance: {}", throwable.getMessage());
        return false;
    }

    default BigDecimal fallbackForCreditAccountBalance(BigDecimal amount, String id, Throwable throwable) {
        log.error("Fallback for credit account balance: {}", throwable.getMessage());
        return BigDecimal.ZERO;
    }

    default BigDecimal fallbackForDebitAccountBalance(BigDecimal amount, String id, Throwable throwable) {
        log.error("Fallback for debit account balance: {}", throwable.getMessage());
        return BigDecimal.ZERO;
    }

    default Boolean fallbackForIsAccountClosed(String id, Throwable throwable) {
        log.error("Fallback for account closed status: {}", throwable.getMessage());
        return false;
    }

}


