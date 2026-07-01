package com.project.transaction.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface TransactionMongoRepository extends MongoRepository<TransactionDocument, String> {
}
