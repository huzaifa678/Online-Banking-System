package com.project.transaction.application.service;

import com.project.transaction.application.dto.TransactionDto;
import com.project.transaction.application.mapper.TransactionDtoMapper;
import com.project.transaction.application.port.in.CreateTransactionUseCase;
import com.project.transaction.application.port.in.GetTransactionUseCase;
import com.project.transaction.application.port.out.TransactionEventPublisherPort;
import com.project.transaction.application.port.out.TransactionRepositoryPort;
import com.project.transaction.application.strategy.TransactionStrategy;
import com.project.transaction.application.strategy.TransactionStrategyFactory;
import com.project.transaction.application.validation.TransactionValidator;
import com.project.transaction.domain.model.Transaction;
import com.project.transaction.domain.vo.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionApplicationService implements CreateTransactionUseCase, GetTransactionUseCase {

    private final TransactionValidator validationChain;
    private final TransactionStrategyFactory strategyFactory;
    private final TransactionRepositoryPort transactionRepository;
    private final TransactionEventPublisherPort eventPublisher;

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        log.info("Received transaction request: {}", transactionDto);

        transactionDto.setTransactionStatus(TransactionStatus.PENDING);

        validationChain.validate(transactionDto);

        Transaction transaction = TransactionDtoMapper.toNewTransaction(transactionDto);

        TransactionStrategy strategy = strategyFactory.getStrategy(transaction.type());
        strategy.execute(transaction);

        transaction.markCompleted();

        Transaction saved = transactionRepository.save(transaction);

        eventPublisher.publishAll(transaction.domainEvents());
        transaction.clearEvents();

        log.info("Transaction processing completed successfully for transaction: {}", saved.transactionId());
        return TransactionDtoMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "transactions", key = "#transactionId", unless = "#result == null")
    public TransactionDto getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .map(TransactionDtoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Transaction ID " + transactionId + " not found."));
    }

    @Override
    @Cacheable(value = "allTransactions")
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionDtoMapper::toDto)
                .toList();
    }
}
