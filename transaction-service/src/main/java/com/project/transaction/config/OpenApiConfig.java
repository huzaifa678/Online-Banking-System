package com.project.transaction.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transactionServiceAPI(){
        return new OpenAPI()
                .info(new Info().title("Transaction Service API")
                        .description("REST API for Transaction Service")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to this documentation for the REST API")
                        .url("https://transaction-service-api-url.com/docs"));
    }
}
