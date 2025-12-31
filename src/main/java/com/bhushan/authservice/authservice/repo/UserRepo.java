package com.bhushan.authservice.authservice.repo;

import com.bhushan.authservice.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);


    Optional<User> findByEmail(String email);
}
