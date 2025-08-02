package com.project.transaction.controller;

import com.project.transaction.exceptions.AccountClosedException;
import com.project.transaction.exceptions.AccountNotFoundException;
import com.project.transaction.exceptions.InsufficientFundsException;
import com.project.transaction.exceptions.TransactionAmountLimitException;
import com.project.transaction.exceptions.TransactionFailedException;
import com.project.transaction.model.dto.TransactionDto;
import com.project.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            TransactionDto dto = transactionService.createTransaction(transactionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto.getTransactionId());
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
            log.error("Insufficient funds error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AccountNotFoundException e) {
            log.error("Account not found error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccountClosedException e) {
            log.error("Account closed error: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (TransactionAmountLimitException e) {
            log.error("Transaction amount limit error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (TransactionFailedException e) {
            log.error("Transaction failed error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable String id) {
        try {
            TransactionDto transactionDto = transactionService.getTransactionById(id);
            return ResponseEntity.ok(transactionDto);
        } catch (Exception e) {
            System.out.println("error occured");
            e.printStackTrace();
            throw new RuntimeException("Could not find the transaction with ID " + id + ": " + e.getMessage(), e);
        }
    }

}

