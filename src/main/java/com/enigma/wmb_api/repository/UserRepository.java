package com.enigma.wmb_api.repository;

import com.enigma.wmb_api.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserAccount, String> {
    UserAccount findByUsernameAndPassword(String username, String password);
    Optional<UserAccount> findByUsername(String username);
}
