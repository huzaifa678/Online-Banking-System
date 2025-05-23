package com.project.payment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.project.payment.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import com.project.payment.model.paymentMethod;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonTypeName("PaymentDto")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class PaymentDto implements Serializable {

    private String paymentId;

    private String source_accountId;

    private String destination_accountId;

    private BigDecimal amount;

    private Status status;

    @JsonProperty("paymentMethod")
    private paymentMethod paymentmethod;

}
