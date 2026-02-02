package com.project.account.controller;

import com.project.account.exceptions.ClosedAccountException;
import com.project.account.exceptions.DuplicateAccountTypesException;
import com.project.account.exceptions.UserNotFoundException;
import com.project.account.model.Dto.AccountsDto;
import com.project.account.model.Status;
import com.project.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<String> addAccount(@RequestBody AccountsDto accountDto) {
        try {
            System.out.println(accountDto);
            accountService.addAccount(accountDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountDto.getAccountId());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ClosedAccountException | DuplicateAccountTypesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountsDto> getAccountById(@PathVariable String id) {
        try {
            AccountsDto accountsDto = accountService.getAccountById(id);
            return ResponseEntity.ok(accountsDto);
        } catch (Exception e) {
            throw new RuntimeException("Could not find the account with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @GetMapping("/allaccounts")
    public ResponseEntity<List<AccountsDto>> getAllAccounts() {
        try {
            List<AccountsDto> accountsLists = accountService.getAllAccounts();
            return ResponseEntity.ok(accountsLists);
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve accounts: " + e.getMessage(), e);
        }
    }

    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable String id, @RequestBody Map<String, String> requestBody) {
        try {
            String status = requestBody.get("status");
            Status statusEnum = Status.valueOf(status.toUpperCase());
            accountService.updateStatus(id, statusEnum);
            return ResponseEntity.status(HttpStatus.OK).body("Account updated successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to update the status of the account with ID: " + id + " Error occurred: " + e);
        }
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<String> closeAccount(@PathVariable String id) {
        try {
            accountService.closeAccount(id);
            return  ResponseEntity.status(HttpStatus.OK).body("Account closed successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to close the account with ID: " + id + " Error occurred: " + e);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<AccountsDto> updateAccount(@PathVariable String id, @RequestBody AccountsDto accountDto) {
        try {
            AccountsDto updateAccount = accountService.updateAccount(id, accountDto);
            return ResponseEntity.ok(updateAccount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update the account with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Account deleted successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the account with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @GetMapping
    public boolean doesAccountExists(@RequestParam String id) {
        if (accountService.doesAccountExists(id) == true) {
            return true;
        } else {
            return false;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean accountBalance(@RequestParam String id, @RequestParam BigDecimal transaction){
        return accountService.accountBalance(id, transaction);
    }

    @PostMapping("/credit")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal creditAccountBalance(@RequestParam BigDecimal amount, @RequestParam String id) {
        return accountService.creditAccountBalance(amount, id);
    }

    @PostMapping("/debit")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal debitAccountBalance(@RequestParam BigDecimal amount, @RequestParam String id) {
        return accountService.debitAccountBalance(amount, id);
    }

    @PostMapping("/isclosed")
    @ResponseStatus(HttpStatus.OK)
    public boolean isAccountClosed(@RequestParam String id) {
        try {
            return accountService.isAccountClosed(id);
        } catch (Exception e) {
            throw new RuntimeException("an error has occurred:"  + e);
        }

    }
}
