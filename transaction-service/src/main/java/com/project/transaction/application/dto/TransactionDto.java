package com.project.transaction.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.transaction.domain.vo.TransactionStatus;
import com.project.transaction.domain.vo.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Boundary data-transfer object for the transaction use cases. Field names and
 * Jackson bindings are kept identical to the previous {@code model.dto.TransactionDto}
 * so the external JSON contract (and Redis cache representation) is unchanged.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto implements Serializable {

    private String transactionId;
    private String source_accountId;
    private String destination_accountId;
    private BigDecimal amount;
    private TransactionStatus transactionStatus;

    @JsonProperty("transactionType")
    @JsonAlias("updateTransactionType")
    private TransactionType transactionType;
}
