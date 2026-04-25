package com.bhushan.authservice.authservice.repo;

import com.bhushan.authservice.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    /**
     * Fetch all users with their profiles eagerly loaded using JOIN FETCH
     * This optimizes the query by fetching user profiles in a single query
     * instead of N+1 queries (1 for users + N for profiles)
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userProfile up " +
           "LEFT JOIN FETCH u.roles r")
    List<User> findAllWithProfiles();

    /**
     * Fetch a specific user with profile and roles eagerly loaded
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userProfile " +
           "LEFT JOIN FETCH u.roles " +
           "WHERE u.userId = :userId")
    Optional<User> findByIdWithProfile(UUID userId);
}
