package com.bhushan.authservice.authservice.repo;

import com.bhushan.authservice.authservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface UserProfileRepo extends JpaRepository<UserProfile, String> {
    boolean existsByMobileNumber(String mobileNumber);
}
