package com.project.payment.model.document;
import com.project.payment.model.Status;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.payment.model.paymentMethod;

@Document(collection = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    private String paymentId;

    private String source_accountId;

    private String destination_accountId;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private Status status;

    private paymentMethod paymentmethod;

}