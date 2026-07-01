package com.project.transaction.interfaces.web;

import com.project.transaction.application.dto.TransactionDto;
import com.project.transaction.application.port.in.CreateTransactionUseCase;
import com.project.transaction.application.port.in.GetTransactionUseCase;
import com.project.transaction.domain.exception.AccountClosedException;
import com.project.transaction.domain.exception.AccountNotFoundException;
import com.project.transaction.domain.exception.InsufficientFundsException;
import com.project.transaction.domain.exception.TransactionAmountLimitException;
import com.project.transaction.domain.exception.TransactionFailedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            TransactionDto dto = createTransactionUseCase.createTransaction(transactionDto);
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
            TransactionDto transactionDto = getTransactionUseCase.getTransactionById(id);
            return ResponseEntity.ok(transactionDto);
        } catch (Exception e) {
            System.out.println("error occured");
            e.printStackTrace();
            throw new RuntimeException("Could not find the transaction with ID " + id + ": " + e.getMessage(), e);
        }
    }
}
