package com.bhushan.authservice.authservice.service;

import com.bhushan.authservice.authservice.entity.Role;
import com.bhushan.authservice.authservice.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);
    private UserLookupService userLookupService;

    @Autowired
    public CustomUserDetailsService(UserLookupService userLookupService)
    {
        this.userLookupService = userLookupService;
    }

    @Override
    public CustomUser loadUserByUsername(String email)
    {
        try {
            logger.debug("Loading user details for email: {}", email);
            User user = userLookupService.getUserByEmailWithFallback(email);
            Set<String> roles = user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());
            logger.debug("Successfully loaded user details for email: {}", email);
            return new CustomUser(user.getEmail(), user.getPassword(), roles);
        } catch (Exception e) {
            logger.error("Failed to load user details for email: {} - {}", email, e.getMessage());
            throw new UsernameNotFoundException("User not found", e);
        }
    }

}
