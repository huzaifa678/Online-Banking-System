package com.project.transaction.processor;

import com.project.transaction.event.TransactionCreatedEvent;
import com.project.transaction.model.Status;
import com.project.transaction.model.document.Transaction;
import com.project.transaction.model.dto.TransactionDto;
import com.project.transaction.model.mapper.TransactionMapper;
import com.project.transaction.repository.TransactionRepository;
import com.project.transaction.strategy.TransactionStrategy;
import com.project.transaction.strategy.TransactionStrategyFactory;
import com.project.transaction.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProcessor {
    
    private final TransactionValidator validationChain;
    private final TransactionStrategyFactory strategyFactory;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;
    
    public TransactionDto processTransaction(TransactionDto transactionDto) {
        log.info("Starting transaction processing for transaction: {}", transactionDto.getTransactionId());
        
        try {
            // Step 1: Set initial status
            transactionDto.setTransactionStatus(Status.PENDING);
            
            // Step 2: Validate transaction
            validateTransaction(transactionDto);
            
            // Step 3: Execute transaction
            executeTransaction(transactionDto);
            
            // Step 4: Persist transaction
            Transaction savedTransaction = persistTransaction(transactionDto);
            
            // Step 5: Publish event
            publishTransactionEvent(savedTransaction);
            
            log.info("Transaction processing completed successfully for transaction: {}", transactionDto.getTransactionId());
            
            return transactionMapper.ConvertToDto(savedTransaction);
            
        } catch (Exception e) {
            log.error("Transaction processing failed for transaction: {}", transactionDto.getTransactionId(), e);
            transactionDto.setTransactionStatus(Status.FAILED);
            throw e;
        }
    }
    
    private void validateTransaction(TransactionDto transactionDto) {
        log.info("Validating transaction: {}", transactionDto.getTransactionId());
        validationChain.validate(transactionDto);
        log.info("Transaction validation passed for transaction: {}", transactionDto.getTransactionId());
    }
    
    private void executeTransaction(TransactionDto transactionDto) {
        log.info("Executing transaction: {}", transactionDto.getTransactionId());
        
        TransactionStrategy strategy = strategyFactory.getStrategy(transactionDto.getTransactionType());
        strategy.execute(transactionDto);
        
        transactionDto.setTransactionStatus(Status.COMPLETED);
        log.info("Transaction execution completed for transaction: {}", transactionDto.getTransactionId());
    }
    
    private Transaction persistTransaction(TransactionDto transactionDto) {
        log.info("Persisting transaction: {}", transactionDto.getTransactionId());
        
        Transaction transaction = transactionMapper.ConvertToDocument(transactionDto);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Transaction persisted successfully: {}", savedTransaction.getTransactionId());
        return savedTransaction;
    }
    
    private void publishTransactionEvent(Transaction transaction) {
        log.info("Publishing transaction event for transaction: {}", transaction.getTransactionId());
        
        TransactionCreatedEvent event = new TransactionCreatedEvent();
        event.setStatus(transaction.getTransactionStatus().toString());
        
        kafkaTemplate.send("transaction-created", event);
        
        log.info("Transaction event published successfully for transaction: {}", transaction.getTransactionId());
    }
} 