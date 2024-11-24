package com.project.api_gateway.routes;


import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.*;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;


@Configuration
public class ApiRoutes {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    @Value("${transaction.service.url}")
    private String transactionServiceUrl;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user-service")
                .route(RequestPredicates.path("/api/users/**"), HandlerFunctions.http(userServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("user-service-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceSwaggerRoute() {
        return route("user-service-swagger")
                .route(RequestPredicates.path("/aggregate/user-service/v3/api-docs"), HandlerFunctions.http(userServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("user-service-swagger-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> accountServiceRoute() {
        return route("account-service")
                .route(RequestPredicates.path("/api/accounts/**"), HandlerFunctions.http(accountServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("account-service-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> accountServiceSwaggerRoute() {
        return route("account-service-swagger")
                .route(RequestPredicates.path("/aggregate/account-service/v3/api-docs"), HandlerFunctions.http(accountServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("account-service-swagger-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> transactionServiceRoute() {
        return route("transaction-service")
                .route(RequestPredicates.path("/api/transaction/**"), HandlerFunctions.http(transactionServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("transaction-service-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> transactionServiceSwaggerRoute() {
        return route("transaction-service-swagger")
                .route(RequestPredicates.path("/aggregate/transaction-service/v3/api-docs"), HandlerFunctions.http(transactionServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("transaction-service-swagger-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoute() {
        return route("payment-service")
                .route(RequestPredicates.path("/api/payment/**"), HandlerFunctions.http(paymentServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("payment-service-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceSwaggerRoute() {
        return route("payment-service-swagger")
                .route(RequestPredicates.path("/aggregate/payment-service/v3/api-docs"), HandlerFunctions.http(paymentServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("payment-service-swagger-circuit-breaker",
                        URI.create("forward:/fallbackRoute")))
                .filter(setPath("/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Service is not available, please try again later"))
                .build();
    }
}
