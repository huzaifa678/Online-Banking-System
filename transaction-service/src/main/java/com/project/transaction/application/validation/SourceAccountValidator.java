package com.project.transaction.application.validation;

import com.project.transaction.application.dto.TransactionDto;
import com.project.transaction.application.port.out.AccountGatewayPort;
import com.project.transaction.domain.exception.AccountClosedException;
import com.project.transaction.domain.exception.AccountNotFoundException;
import com.project.transaction.domain.exception.InsufficientFundsException;
import com.project.transaction.domain.vo.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class SourceAccountValidator extends TransactionValidator {

    private final AccountGatewayPort accountGateway;

    @Override
    public void validate(TransactionDto transactionDto) {
        TransactionType type = transactionDto.getTransactionType();

        if (type == TransactionType.WITHDRAWAL || type == TransactionType.TRANSFER) {
            String sourceAccountId = transactionDto.getSource_accountId();

            log.info("Validating source account: {}", sourceAccountId);

            if (!accountGateway.doesAccountExists(sourceAccountId)) {
                throw new AccountNotFoundException("The source Account with ID: " + sourceAccountId + " does not exist");
            }

            if (!accountGateway.accountBalance(sourceAccountId, transactionDto.getAmount())) {
                throw new InsufficientFundsException("You do not have enough balance to make the transaction");
            }

            if (accountGateway.isAccountClosed(sourceAccountId)) {
                throw new AccountClosedException("The source Account with ID: " + sourceAccountId + " is closed");
            }

            log.info("Source account validation passed for account: {}", sourceAccountId);
        }

        validateNext(transactionDto);
    }
}
