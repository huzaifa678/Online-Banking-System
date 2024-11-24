package com.project.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.transaction.model.Status;
import com.project.transaction.model.TransactionTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
    private String transactionId;
    private String source_accountId;
    private String destination_accountId;
    private BigDecimal amount;
    private Status transactionStatus;
    @JsonProperty("transactionType")
    @JsonAlias("updateTransactionType")
    private TransactionTypes transactionType;

}
