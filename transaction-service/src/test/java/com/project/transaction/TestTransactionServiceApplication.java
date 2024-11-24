package com.project.transaction;

import org.springframework.boot.SpringApplication;

public class TestTransactionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TransactionServiceApplication::main).with(TestTransactionServiceApplication.class).run(args);
	}

}
