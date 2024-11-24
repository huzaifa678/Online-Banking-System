package com.project.user.model.Entity;

import com.project.user.model.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long user_id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    @CreationTimestamp
    private LocalDate created_at;

    @Column(name = "updated_at", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    @UpdateTimestamp
    private LocalDate updated_at;

    @Enumerated(EnumType.STRING)
    private Status status;


}

