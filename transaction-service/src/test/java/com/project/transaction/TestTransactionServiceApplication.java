package com.project.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class TestTransactionServiceApplication {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
		kafka.start();
		return kafka;
	}

	public static void main(String[] args) {
		SpringApplication.from(TransactionServiceApplication::main).with(TestTransactionServiceApplication.class).run(args);
	}

}
