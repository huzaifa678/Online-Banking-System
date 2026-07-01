package com.project.transaction.infrastructure.persistence;

import com.project.transaction.domain.model.Transaction;
import com.project.transaction.domain.vo.AccountId;
import com.project.transaction.domain.vo.Money;


final class TransactionPersistenceMapper {

    private TransactionPersistenceMapper() {
    }

    static Transaction toDomain(TransactionDocument document) {
        return Transaction.reconstitute(
                document.getTransactionId(),
                AccountId.ofNullable(document.getSource_accountId()),
                AccountId.ofNullable(document.getDestination_accountId()),
                Money.of(document.getAmount()),
                document.getTransactionType(),
                document.getTransactionStatus());
    }

    static TransactionDocument toDocument(Transaction transaction) {
        return TransactionDocument.builder()
                .transactionId(transaction.transactionId())
                .source_accountId(transaction.sourceAccountId() == null ? null : transaction.sourceAccountId().value())
                .destination_accountId(transaction.destinationAccountId() == null ? null : transaction.destinationAccountId().value())
                .amount(transaction.amount().amount())
                .transactionStatus(transaction.status())
                .transactionType(transaction.type())
                .build();
    }
}
