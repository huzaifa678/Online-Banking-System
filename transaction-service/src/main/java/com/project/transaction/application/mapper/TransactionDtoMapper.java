package com.project.transaction.application.mapper;

import com.project.transaction.application.dto.TransactionDto;
import com.project.transaction.domain.model.Transaction;
import com.project.transaction.domain.vo.AccountId;
import com.project.transaction.domain.vo.Money;


public final class TransactionDtoMapper {

    private TransactionDtoMapper() {
    }

    public static TransactionDto toDto(Transaction tx) {
        return TransactionDto.builder()
                .transactionId(tx.transactionId())
                .source_accountId(tx.sourceAccountId() == null ? null : tx.sourceAccountId().value())
                .destination_accountId(tx.destinationAccountId() == null ? null : tx.destinationAccountId().value())
                .amount(tx.amount().amount())
                .transactionStatus(tx.status())
                .transactionType(tx.type())
                .build();
    }

    /**
     * Start a new {@link Transaction} aggregate (status PENDING) from an incoming DTO.
     */
    public static Transaction toNewTransaction(TransactionDto dto) {
        return Transaction.initiate(
                dto.getTransactionId(),
                AccountId.ofNullable(dto.getSource_accountId()),
                AccountId.ofNullable(dto.getDestination_accountId()),
                Money.of(dto.getAmount()),
                dto.getTransactionType());
    }
}
