package com.project.transaction.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.math.BigDecimal;

import groovy.util.logging.Slf4j;


@Slf4j
public interface AccountClient {

    Logger log = LoggerFactory.getLogger(AccountClient.class);


    @GetExchange("/api/accounts")
    @CircuitBreaker(name = "accountExists", fallbackMethod = "fallbackForDoesAccountExists")
    @Retry(name = "accountExists")
    boolean doesAccountExists(@RequestParam String id);


    @PostExchange("/api/accounts")
    @CircuitBreaker(name = "accountBalance", fallbackMethod = "fallbackForAccountBalance")
    @Retry(name = "accountBalance")
    boolean accountBalance(@RequestParam String id, @RequestParam BigDecimal transaction);


    @PostExchange("/api/accounts/credit")
    @CircuitBreaker(name = "accountCredit", fallbackMethod = "fallbackForCreditAccountBalance")
    @Retry(name = "accountCredit")
    BigDecimal creditAccountBalance(@RequestParam BigDecimal amount, @RequestParam String id);


    @PostExchange("/api/accounts/debit")
    @CircuitBreaker(name = "accountDebit", fallbackMethod = "fallbackForDebitAccountBalance")
    @Retry(name = "accountDebit")
    BigDecimal debitAccountBalance(@RequestParam BigDecimal amount, @RequestParam String id);


    @PostExchange("/api/accounts/isclosed")
    @CircuitBreaker(name = "accountClosed", fallbackMethod = "fallbackForIsAccountClosed")
    @Retry(name = "accountClosed")
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


