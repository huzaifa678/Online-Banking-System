package com.project.account.model.Mapper;

import com.project.account.model.Dto.AccountsDto;
import com.project.account.model.entity.Accounts;
import org.springframework.stereotype.Component;


@Component
public class AccountMapper extends BaseMapper<Accounts, AccountsDto>{
    @Override
    public Accounts ConvertToEntity(AccountsDto dto, Object... args) {
        return Accounts.builder()
                .accountId(dto.getAccountId())
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .status(dto.getStatus())
                .userEmail(dto.getUserEmail())
                .build();

    }

    @Override
    public AccountsDto ConvertToDto(Accounts entity, Object... args) {
        return AccountsDto.builder()
                .accountId(entity.getAccountId())
                .accountType(entity.getAccountType())
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .userEmail(entity.getUserEmail())
                .build();
    }
}
