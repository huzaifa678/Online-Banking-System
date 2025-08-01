package com.project.account.config;


import com.project.account.validator.AccountActiveValidator;
import com.project.account.validator.AccountClosedValidator;
import com.project.account.validator.AccountInactiveValidator;
import com.project.account.validator.AccountValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountValidatorConfig {

    @Bean
    public AccountValidator accountValidatorChain(
            AccountClosedValidator accountClosedValidator,
            AccountInactiveValidator accountInactiveValidator,
            AccountActiveValidator accountActiveValidator
    ) {
        accountClosedValidator.setNext(accountInactiveValidator);
        accountInactiveValidator.setNext(accountActiveValidator);
        return accountClosedValidator;
    }


}
