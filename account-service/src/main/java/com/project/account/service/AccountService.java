package com.project.account.service;

import com.project.account.client.UserClient;
import com.project.account.event.AccountClosedEvent;
import com.project.account.event.AccountCreatedEvent;
import com.project.account.event.AccountUpdatedEvent;
import com.project.account.exceptions.*;
import com.project.account.model.Status;
import com.project.account.model.entity.Accounts;
import com.project.account.model.Dto.AccountsDto;
import com.project.account.model.Mapper.AccountMapper;
import com.project.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    private final UserClient userClient;

    private AccountsDto accountsDto;

    private final KafkaTemplate<String, Object> kafkaTemplate;



    public void addAccount(AccountsDto accountsDto) {
        System.out.println("received dto: " + accountsDto);

        if (userClient.doesUserExist(accountsDto.getUserEmail())) {
            Optional<Accounts> existingAccount = accountRepository
                    .findByUserEmailAndAccountType(accountsDto.getUserEmail(), accountsDto.getAccountType());

            if (existingAccount.isPresent()) {

                Accounts accounts = existingAccount.get();

                System.out.println("Existing account found: " + accounts);

                switch (accounts.getStatus()) {
                    case CLOSED:
                        System.out.println("Account is closed");
                        throw new ClosedAccountException("Account is closed for User Email: " + accountsDto.getUserEmail() + ". Please create a new account to proceed.");

                    case INACTIVE:
                        System.out.println("Account is inactive");
                        accounts.setStatus(Status.ACTIVE);
                        accountRepository.save(accounts);
                        break;

                    case ACTIVE:
                        System.out.println("Account already exists and is active");
                        throw new DuplicateAccountTypesException("Account type " + accountsDto.getAccountType() + " already exists for User ID: " + accountsDto.getUserEmail());

                    default:
                        throw new IllegalStateException("Unexpected value: " + accounts.getStatus());
                }

            } else {
                Accounts newAccount = accountMapper.ConvertToEntity(accountsDto);
                newAccount.setStatus(Status.ACTIVE);
                System.out.println("received entity: " + accountsDto);
                accountRepository.save(newAccount);

                AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent();
                accountCreatedEvent.setAccountId(newAccount.getAccountId());
                accountCreatedEvent.setUserEmail(newAccount.getUserEmail());
                log.info("Started sending AccountCreatedEvent {} to kafka topic account-created", accountCreatedEvent);
                kafkaTemplate.send("account-created", accountCreatedEvent);
                log.info("Ended sending AccountCreatedEvent {} to kafka topic account-created", accountCreatedEvent);
            }

        } else {
            throw new UserNotFoundException("The User Email " + accountsDto.getUserEmail() + " does not exist");
        }

    }

    public void closeAccount(String id) {
        Optional<Accounts> accountOptional = accountRepository.findById(id);

        if (!accountOptional.isPresent()) {
            throw new AccountNotFoundException("Account with ID " + id + " not found");
        }

        Accounts account = accountOptional.get();

        if (account.getStatus() == Status.CLOSED) {
            throw new IllegalStateException("Account is already closed");
        }

        account.setStatus(Status.CLOSED);
        accountRepository.save(account);

        AccountClosedEvent accountClosedEvent = new AccountClosedEvent();
        accountClosedEvent.setAccountId(account.getAccountId());
        accountClosedEvent.setUserEmail(account.getUserEmail());
        log.info("Started sending AccountClosedEvent {} to kafka topic account-closed", accountClosedEvent);
        kafkaTemplate.send("account-closed", accountClosedEvent);
        log.info("Ended sending AccountClosedEvent {} to kafka topic account-closed", accountClosedEvent);

        System.out.println("Account with ID " + id + " has been closed.");
    }

    public void updateStatus(String id, Status status) {

        Optional<Accounts> accountOptional = accountRepository.findById(id);

        if (!accountOptional.isPresent()) {
            throw new AccountNotFoundException("Account with ID " + id + " not found");
        }

        Accounts account = accountOptional.get();

        if (account.getStatus() == status) {
            throw new SameStatusException("This status for the account already exists");
        }

        account.setStatus(status);
        accountRepository.save(account);

        System.out.println("Account with ID " + id + " Status has been updated to " + account.getStatus());

    }

    public AccountsDto getAccountById(String accountId) {
        Optional<Accounts> accountOptional = accountRepository.findById(accountId);

        try {
            if (accountOptional.isPresent()) {
                return accountMapper.ConvertToDto(accountOptional.get());
            } else {
                throw new AccountNotFoundException("Account ID: " + accountId + " not found.");
            }
        } catch (Exception e) {
            throw new AccountNotFoundException("Failed to find the account ID: " + accountId + "error occurred: " + e);
        }

    }

    public List<AccountsDto> getAllAccounts() {
        List<Accounts> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::ConvertToDto)
                .collect(Collectors.toList());
    }

    public AccountsDto updateAccount(String accountId, AccountsDto accountDto) {
        Optional<Accounts> accountOptional = accountRepository.findById(accountId);

        try {
            if (accountOptional.isPresent()) {
                Accounts account = accountOptional.get();

                account.setAccountType(accountDto.getAccountType());
                account.setBalance(accountDto.getBalance());
                account.setStatus(accountDto.getStatus());

                accountRepository.save(account);

                return accountMapper.ConvertToDto(account);
            } else {
                throw new AccountNotFoundException("Failed to update account with ID: " + accountId + ".");
            }
        } catch (Exception e) {
            throw new AccountNotFoundException("Failed to update account with ID: " + accountId + "error occurred: " + e);
        }

    }

    public void deleteAccount(String accountId) {
        Optional<Accounts> userOptional = accountRepository.findById(accountId);


        try {
            if (userOptional.isPresent()) {
                accountRepository.deleteById(accountId);
            } else {
                throw new RuntimeException("Failed to delete account with ID: " + accountId + ".");
            }
        } catch (Exception e) {
            throw new AccountNotFoundException("Failed to delete the account with ID: " + accountId + "error occurred: " + e);
        }

    }

    public boolean doesAccountExists(String id) {
        boolean ifAccountExists = accountRepository.existsById(id);
        return ifAccountExists;
    }

    public boolean accountBalance(String id, BigDecimal transaction) {
        Optional<Accounts> accountOptional = accountRepository.findById(id);
        boolean isAccountBalanceEnough = false;
        if (accountOptional.isPresent()) {
            Accounts accounts = accountOptional.get();

            BigDecimal accountsBalance = accounts.getBalance();

            isAccountBalanceEnough = (accountsBalance.equals(transaction) || transaction.compareTo(accountsBalance) == -1);

        }

        return isAccountBalanceEnough;
    }

    public BigDecimal creditAccountBalance(BigDecimal amount, String id) {
        Optional<Accounts> accountOptional = accountRepository.findById(id);

        if (accountOptional.isPresent()) {
            Accounts accounts = accountOptional.get();

            BigDecimal creditedAccountBalance = accounts.getBalance().subtract(amount);

            accounts.setBalance(creditedAccountBalance);

            accountRepository.save(accounts);

            return creditedAccountBalance;
        } else {
            throw new AccountNotFoundException("The account which needs to be credited with ID: " + id + " does not exist");
        }

    }

    public BigDecimal debitAccountBalance(BigDecimal amount, String id) {
        Optional<Accounts> accountOptional = accountRepository.findById(id);

        if (accountOptional.isPresent()) {
            Accounts accounts = accountOptional.get();

            BigDecimal debitedAccountBalance = accounts.getBalance().add(amount);

            accounts.setBalance(debitedAccountBalance);

            accountRepository.save(accounts);

            return debitedAccountBalance;
        } else {
            throw new AccountNotFoundException("The account which needs to be debited with ID: " + id + " does not exist");
        }

    }

    public boolean isAccountClosed(String id) {

        Optional<Accounts> accountOptional = accountRepository.findById(id);

        if (accountOptional.isPresent()) {
            Accounts accounts = accountOptional.get();

            boolean ifClosed = (accounts.getStatus() == Status.CLOSED);

            return ifClosed;
        }

        throw new AccountNotFoundException("The account does not exist with ID: " + id);
    }


}