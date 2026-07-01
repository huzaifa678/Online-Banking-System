package com.project.payment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.project.payment.domain.vo.PaymentMethod;
import com.project.payment.domain.vo.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Boundary data-transfer object for the payment use cases. Field names and
 * Jackson bindings are unchanged from the previous {@code model.dto.PaymentDto}
 * so external JSON and the Redis cache representation stay identical.
 */
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

    private PaymentStatus status;

    @JsonProperty("paymentMethod")
    private PaymentMethod paymentmethod;
}
