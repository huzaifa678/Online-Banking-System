package com.project.transaction.infrastructure.persistence;

import com.project.transaction.domain.vo.TransactionStatus;
import com.project.transaction.domain.vo.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Document(collection = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDocument {

    @Id
    private String transactionId;
    private String source_accountId;
    private String destination_accountId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
}
