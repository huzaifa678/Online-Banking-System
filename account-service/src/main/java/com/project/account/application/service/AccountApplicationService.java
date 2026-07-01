package com.project.account.application.service;

import com.project.account.application.dto.AccountsDto;
import com.project.account.application.mapper.AccountDtoMapper;
import com.project.account.application.port.in.AccountBalanceUseCase;
import com.project.account.application.port.in.AccountCommandUseCase;
import com.project.account.application.port.in.AccountQueryUseCase;
import com.project.account.application.port.out.AccountEventPublisherPort;
import com.project.account.application.port.out.AccountRepositoryPort;
import com.project.account.application.port.out.UserGatewayPort;
import com.project.account.domain.exception.AccountNotFoundException;
import com.project.account.domain.exception.UserNotFoundException;
import com.project.account.domain.model.Account;
import com.project.account.domain.vo.AccountId;
import com.project.account.domain.vo.AccountStatus;
import com.project.account.domain.vo.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountApplicationService
        implements AccountCommandUseCase, AccountQueryUseCase, AccountBalanceUseCase {

    private final AccountRepositoryPort accountRepository;
    private final UserGatewayPort userGateway;
    private final AccountEventPublisherPort eventPublisher;

    @Override
    public void addAccount(AccountsDto accountsDto) {
        log.info("Received DTO: {}", accountsDto);

        if (!userGateway.doesUserExist(accountsDto.getUserEmail())) {
            throw new UserNotFoundException(
                    "The User Email " + accountsDto.getUserEmail() + " does not exist");
        }

        Optional<Account> existingAccountOpt = accountRepository
                .findByUserEmailAndAccountType(accountsDto.getUserEmail(), accountsDto.getAccountType());

        if (existingAccountOpt.isPresent()) {
            Account existingAccount = existingAccountOpt.get();
            log.info("Existing account found: {}", existingAccount.id());
            // Reactivates an inactive account, or throws for active/closed accounts.
            existingAccount.ensureCanReRegister();
            accountRepository.save(existingAccount);
        } else {
            Account newAccount = AccountDtoMapper.toNewAccount(accountsDto);
            accountRepository.save(newAccount);
            publishEvents(newAccount);
        }
    }

    @Override
    public void closeAccount(String id) {
        Account account = loadOrThrow(id);
        account.close();
        accountRepository.save(account);
        publishEvents(account);
        log.info("Account with ID {} has been closed.", id);
    }

    @Override
    public void updateStatus(String id, AccountStatus status) {
        Account account = loadOrThrow(id);
        account.changeStatus(status);
        accountRepository.save(account);
        log.info("Account with ID {} status has been updated to {}", id, account.status());
    }

    @Override
    public AccountsDto updateAccount(String accountId, AccountsDto accountDto) {
        Account account = loadOrThrow(accountId);
        account.updateDetails(
                accountDto.getAccountType(),
                accountDto.getBalance() == null ? account.balance() : Money.of(accountDto.getBalance()),
                accountDto.getStatus());
        Account saved = accountRepository.save(account);
        return AccountDtoMapper.toDto(saved);
    }

    @Override
    public void deleteAccount(String accountId) {
        AccountId id = AccountId.of(accountId);
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Failed to delete account with ID: " + accountId + ".");
        }
        accountRepository.deleteById(id);
    }

    @Override
    public AccountsDto getAccountById(String accountId) {
        return AccountDtoMapper.toDto(loadOrThrow(accountId));
    }

    @Override
    public List<AccountsDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountDtoMapper::toDto)
                .toList();
    }

    @Override
    public boolean doesAccountExists(String id) {
        return accountRepository.existsById(AccountId.of(id));
    }

    @Override
    public boolean accountBalance(String id, BigDecimal transaction) {
        return accountRepository.findById(AccountId.of(id))
                .map(account -> account.hasSufficientBalance(Money.of(transaction)))
                .orElse(false);
    }

    @Override
    public BigDecimal creditAccountBalance(BigDecimal amount, String id) {
        Account account = accountRepository.findById(AccountId.of(id))
                .orElseThrow(() -> new AccountNotFoundException(
                        "The account which needs to be credited with ID: " + id + " does not exist"));
        account.withdraw(Money.of(amount));
        return accountRepository.save(account).balance().amount();
    }

    @Override
    public BigDecimal debitAccountBalance(BigDecimal amount, String id) {
        Account account = accountRepository.findById(AccountId.of(id))
                .orElseThrow(() -> new AccountNotFoundException(
                        "The account which needs to be debited with ID: " + id + " does not exist"));
        account.deposit(Money.of(amount));
        return accountRepository.save(account).balance().amount();
    }

    @Override
    public boolean isAccountClosed(String id) {
        return accountRepository.findById(AccountId.of(id))
                .orElseThrow(() -> new AccountNotFoundException(
                        "The account does not exist with ID: " + id))
                .isClosed();
    }

    private Account loadOrThrow(String id) {
        return accountRepository.findById(AccountId.of(id))
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + id + " not found"));
    }

    private void publishEvents(Account account) {
        if (!account.domainEvents().isEmpty()) {
            eventPublisher.publishAll(account.domainEvents());
            account.clearEvents();
        }
    }
}
