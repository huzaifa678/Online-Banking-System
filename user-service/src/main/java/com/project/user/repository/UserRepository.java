package com.project.user.repository;

import com.project.user.model.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByemail(String email);
    boolean existsByemail(String email);
}
