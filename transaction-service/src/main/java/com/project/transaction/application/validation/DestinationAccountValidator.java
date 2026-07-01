package com.project.transaction.application.validation;

import com.project.transaction.application.dto.TransactionDto;
import com.project.transaction.application.port.out.AccountGatewayPort;
import com.project.transaction.domain.exception.AccountClosedException;
import com.project.transaction.domain.exception.AccountNotFoundException;
import com.project.transaction.domain.vo.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class DestinationAccountValidator extends TransactionValidator {

    private final AccountGatewayPort accountGateway;

    @Override
    public void validate(TransactionDto transactionDto) {
        TransactionType type = transactionDto.getTransactionType();

        if (type == TransactionType.DEPOSIT || type == TransactionType.TRANSFER) {
            String destinationAccountId = transactionDto.getDestination_accountId();

            log.info("Validating destination account: {}", destinationAccountId);

            if (!accountGateway.doesAccountExists(destinationAccountId)) {
                throw new AccountNotFoundException("The destination Account with ID: " + destinationAccountId + " does not exist");
            }

            if (accountGateway.isAccountClosed(destinationAccountId)) {
                throw new AccountClosedException("The destination Account with ID: " + destinationAccountId + " is closed");
            }

            log.info("Destination account validation passed for account: {}", destinationAccountId);
        }

        validateNext(transactionDto);
    }
}
