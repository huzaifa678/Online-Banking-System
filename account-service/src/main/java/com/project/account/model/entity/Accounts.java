package com.project.account.model.entity;

import com.project.account.model.AccountTypes;
import com.project.account.model.Status;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Accounts {

    @Id
    private String accountId;

    @Enumerated(EnumType.STRING)
    private AccountTypes accountType;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String userEmail;

}
