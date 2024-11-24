package com.project.payment.repository;

import com.project.payment.model.document.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface PaymentRepository extends MongoRepository<Payment, String> {
}
