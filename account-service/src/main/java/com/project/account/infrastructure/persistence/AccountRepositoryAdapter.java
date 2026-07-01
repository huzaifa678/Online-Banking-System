package com.project.account.infrastructure.persistence;

import com.project.account.application.port.out.AccountRepositoryPort;
import com.project.account.domain.model.Account;
import com.project.account.domain.vo.AccountId;
import com.project.account.domain.vo.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;

    @Override
    public Account save(Account account) {
        AccountEntity saved = jpaRepository.save(AccountPersistenceMapper.toEntity(account));
        return AccountPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return jpaRepository.findById(id.value()).map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Account> findByUserEmailAndAccountType(String userEmail, AccountType accountType) {
        return jpaRepository.findByUserEmailAndAccountType(userEmail, accountType)
                .map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll().stream()
                .map(AccountPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(AccountId id) {
        return jpaRepository.existsById(id.value());
    }

    @Override
    public void deleteById(AccountId id) {
        jpaRepository.deleteById(id.value());
    }
}
