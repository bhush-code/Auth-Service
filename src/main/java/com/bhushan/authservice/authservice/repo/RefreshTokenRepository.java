package com.bhushan.authservice.authservice.repo;

import com.bhushan.authservice.authservice.entity.RefreshToken;
import com.bhushan.authservice.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository  extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);
//    void deleteByUser(User user);
@Modifying
@Query("DELETE FROM RefreshToken r WHERE r.user.userId = :userId")
void deleteByUserUserId(@Param("userId") UUID userId);

    RefreshToken findByUserUserId(UUID userId);
}
