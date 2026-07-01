package com.project.transaction.infrastructure.persistence;

import com.project.transaction.application.port.out.TransactionRepositoryPort;
import com.project.transaction.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionMongoRepository mongoRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionDocument saved = mongoRepository.save(TransactionPersistenceMapper.toDocument(transaction));
        return TransactionPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        return mongoRepository.findById(transactionId).map(TransactionPersistenceMapper::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return mongoRepository.findAll().stream()
                .map(TransactionPersistenceMapper::toDomain)
                .toList();
    }
}
