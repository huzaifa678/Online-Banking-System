package com.project.account.application.dto;

import com.project.account.domain.vo.AccountStatus;
import com.project.account.domain.vo.AccountType;
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

    private AccountType accountType;

    private BigDecimal balance;

    private AccountStatus status;

    private String userEmail;
}
