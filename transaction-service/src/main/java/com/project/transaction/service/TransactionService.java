package com.project.transaction.service;
import com.project.transaction.model.document.Transaction;
import com.project.transaction.model.dto.TransactionDto;
import com.project.transaction.model.mapper.TransactionMapper;
import com.project.transaction.processor.TransactionProcessor;
import com.project.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionProcessor transactionProcessor;

    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionDto createTransaction(TransactionDto transactionDto) throws ConnectException {
        log.info("Received transaction request: {}", transactionDto);

        return transactionProcessor.processTransaction(transactionDto);
        
//        try {
//            return transactionProcessor.processTransaction(transactionDto);
//        } catch (Exception e) {
//            log.error("Transaction creation failed", e);
//            throw new TransactionFailedException("Transaction failed: " + e.getMessage(), e);
//        }
    }

    @Cacheable(value = "transactions", key = "#transactionId", unless = "#result == null")
    public TransactionDto getTransactionById(String transactionId) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);

        try {
            if (transactionOptional.isPresent()) {
                return transactionMapper.ConvertToDto(transactionOptional.get());
            } else {
                throw new RuntimeException("Transaction ID " + transactionId + " not found.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find the transaction ID: " + transactionId + "error occurred: " + e);
        }
    }

    @Cacheable(value = "allTransactions")
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::ConvertToDto)
                .collect(Collectors.toList());
    }
}
