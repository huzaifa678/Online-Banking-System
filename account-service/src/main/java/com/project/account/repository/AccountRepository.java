package com.project.account.repository;

import com.project.account.model.AccountTypes;
import com.project.account.model.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Accounts, String> {
    Optional<Accounts> findByUserEmailAndAccountType(String userEmail, AccountTypes accountType);
    Optional<Accounts> findByUserEmail(String userEmail);
}
