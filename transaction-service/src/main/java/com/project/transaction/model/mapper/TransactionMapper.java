package com.project.transaction.model.mapper;

import com.project.transaction.model.document.Transaction;
import com.project.transaction.model.dto.TransactionDto;
import org.springframework.stereotype.Component;


@Component
public class TransactionMapper extends BaseMapper<Transaction, TransactionDto> {

    @Override
    public Transaction ConvertToDocument(TransactionDto dto, Object... args) {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .transactionId(dto.getTransactionId())
                .amount(dto.getAmount())
                .transactionStatus(dto.getTransactionStatus())
                .transactionType(dto.getTransactionType());

        if (dto.getSource_accountId() != null) {
            builder.source_accountId(dto.getSource_accountId());
        }

        if (dto.getDestination_accountId() != null) {
            builder.destination_accountId(dto.getDestination_accountId());
        }

        return builder.build();
    }

    @Override
    public TransactionDto ConvertToDto(Transaction document, Object... args) {
        return TransactionDto.builder()
                .transactionId(document.getTransactionId())
                .source_accountId(document.getSource_accountId())
                .destination_accountId(document.getDestination_accountId())
                .amount(document.getAmount())
                .transactionStatus(document.getTransactionStatus())
                .transactionType(document.getTransactionType())
                .build();
    }
}
