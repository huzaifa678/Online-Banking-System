package com.project.account.infrastructure.persistence;

import com.project.account.domain.vo.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccountJpaRepository extends JpaRepository<AccountEntity, String> {

    Optional<AccountEntity> findByUserEmailAndAccountType(String userEmail, AccountType accountType);

    Optional<AccountEntity> findByUserEmail(String userEmail);
}
