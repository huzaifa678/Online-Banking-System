package com.project.account.model.Dto;

import com.project.account.model.AccountTypes;
import com.project.account.model.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountsDto {


    private String accountId;

    private AccountTypes accountType;

    private BigDecimal balance;

    private Status status;

    private String userEmail;


}
