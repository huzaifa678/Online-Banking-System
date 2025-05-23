package com.project.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

@TestConfiguration(proxyBeanMethods = false)
class AccountServiceApplicationTests {

	@Bean
	@ServiceConnection
	MySQLContainer<?> mySQLContainer() {
		return new MySQLContainer<>(DockerImageName.parse("mysql:latest"));
	}

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
		kafka.start();
		return kafka;
	}

	public static void main(String[] args) {
		SpringApplication.from(AccountServiceApplication::main).with(AccountServiceApplicationTests.class).run(args);
	}

}
