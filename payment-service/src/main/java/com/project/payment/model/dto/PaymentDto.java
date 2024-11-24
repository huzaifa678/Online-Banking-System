package com.project.payment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.payment.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import com.project.payment.model.paymentMethod;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {

    private String paymentId;

    private String source_accountId;

    private String destination_accountId;

    private BigDecimal amount;

    private Status status;

    @JsonProperty("paymentMethod")
    private paymentMethod paymentmethod;

}
