package com.project.payment;

import org.springframework.boot.SpringApplication;

public class TestPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentServiceApplication::main).with(TestPaymentServiceApplication.class).run(args);
	}

}
