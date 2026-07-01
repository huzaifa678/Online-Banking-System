package com.project.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private String cardNumber;
    private long expiryMonth;
    private long expiryYear;
    private String cvc;
}
