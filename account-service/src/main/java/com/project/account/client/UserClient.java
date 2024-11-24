package com.project.account.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import groovy.util.logging.Slf4j;

@Slf4j
public interface UserClient {

    Logger log = LoggerFactory.getLogger(UserClient.class);

    @GetExchange("/api/users")
    @CircuitBreaker(name = "User", fallbackMethod = "fallbackMethod")
    @Retry(name = "User")
    boolean doesUserExist(@RequestParam String email);

    default boolean fallbackMethod(String email, Throwable throwable) {
        log.info("cannot get response from the user client", email, throwable.getMessage());
        return false;
    }

}
