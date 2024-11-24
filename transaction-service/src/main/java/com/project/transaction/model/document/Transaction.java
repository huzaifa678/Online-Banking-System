package com.project.transaction.model.document;
import com.project.transaction.model.Status;

import com.project.transaction.model.TransactionTypes;
import lombok.*;
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
public class Transaction {
    @Id
    private String transactionId;
    private String source_accountId;
    private String destination_accountId;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private Status transactionStatus;
    private TransactionTypes transactionType;

}
