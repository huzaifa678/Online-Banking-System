package com.project.payment.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface PaymentRepository extends MongoRepository<PaymentDocument, String> {
}
