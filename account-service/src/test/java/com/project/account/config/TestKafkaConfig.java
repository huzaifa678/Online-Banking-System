package com.project.account.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@Profile("test")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true"
})
public class TestKafkaConfig {

    @Bean(name = "testKafkaTemplate")
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }

}