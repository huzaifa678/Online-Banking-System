package com.project.transaction.service;
import com.project.transaction.client.AccountClient;
import com.project.transaction.event.TransactionCreatedEvent;
import com.project.transaction.exceptions.AccountClosedException;
import com.project.transaction.exceptions.AccountNotFoundException;
import com.project.transaction.exceptions.InsufficientFundsException;
import com.project.transaction.model.Status;
import com.project.transaction.model.TransactionTypes;
import com.project.transaction.model.document.Transaction;
import com.project.transaction.model.dto.TransactionDto;
import com.project.transaction.model.mapper.TransactionMapper;
import com.project.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class TransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final TransactionMapper transactionMapper;

    @Autowired
    private final AccountClient accountClient;

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    public TransactionDto createTransaction(TransactionDto transactionDto) throws ConnectException {
        String sourceAccountId = transactionDto.getSource_accountId();
        String destinationAccountId = transactionDto.getDestination_accountId();
        BigDecimal amount = transactionDto.getAmount();
        TransactionTypes type = transactionDto.getTransactionType();

        System.out.println("received data to server " + transactionDto);

        transactionDto.setTransactionStatus(Status.PENDING);

        if (type == TransactionTypes.TRANSFER || type == TransactionTypes.WITHDRAWAL) {
            if (!accountClient.doesAccountExists(sourceAccountId)) {
                transactionDto.setTransactionStatus(Status.FAILED);
                throw new RuntimeException("The source Account with ID: " + sourceAccountId + " does not exist");
            }

            if (!accountClient.accountBalance(sourceAccountId, amount)) {
                transactionDto.setTransactionStatus(Status.FAILED);
                throw new InsufficientFundsException("You do not have enough balance to make the transaction");
            }

            if (accountClient.isAccountClosed(sourceAccountId)) {
                transactionDto.setTransactionStatus(Status.CANCELLED);
                throw new AccountClosedException("The source Account with ID: " + sourceAccountId + " is closed");
            }
        }
        if (type == TransactionTypes.TRANSFER || type == TransactionTypes.DEPOSIT) {
            if (!accountClient.doesAccountExists(sourceAccountId)) {
                if (sourceAccountId == null) {
                    transactionDto.setTransactionStatus(Status.FAILED);
                    throw new AccountNotFoundException("The destination Account with ID: " + destinationAccountId + " does not exist");
                }
            }

            if (accountClient.isAccountClosed(sourceAccountId)) {
                transactionDto.setTransactionStatus(Status.CANCELLED);
                throw new AccountClosedException("The destination Account with ID: " + destinationAccountId + " is closed");
            }
        }

        switch (type) {
            case DEPOSIT:
                accountClient.debitAccountBalance(amount, sourceAccountId);
                break;
            case WITHDRAWAL:
                accountClient.creditAccountBalance(amount, sourceAccountId);
                break;
            case TRANSFER:
                accountClient.debitAccountBalance(amount, sourceAccountId);
                accountClient.creditAccountBalance(amount, destinationAccountId);
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + type);
        }

        transactionDto.setTransactionStatus(Status.COMPLETED);
        Transaction transaction = transactionMapper.ConvertToDocument(transactionDto);
        Transaction savedTransaction = transactionRepository.save(transaction);

        String status = transaction.getTransactionStatus().toString();

        TransactionCreatedEvent transactionCreatedEvent = new TransactionCreatedEvent();
        transactionCreatedEvent.setStatus(status);
        log.info("Started sending TransactionCreatedEvent {} to kafka topic transaction-created", transactionCreatedEvent);
        kafkaTemplate.send("transaction-created", transactionCreatedEvent);
        log.info("Ended sending TransactionCreatedEvent {} to kafka topic transaction-created", transactionCreatedEvent);

        return transactionMapper.ConvertToDto(savedTransaction);
    }


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

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::ConvertToDto)
                .collect(Collectors.toList());
    }



}
