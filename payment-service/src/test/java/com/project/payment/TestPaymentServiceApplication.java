package com.project.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class TestPaymentServiceApplication {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));
		kafka.start();
		return kafka;
	}

	public static void main(String[] args) {
		SpringApplication.from(PaymentServiceApplication::main).with(TestPaymentServiceApplication.class).run(args);
	}

}
