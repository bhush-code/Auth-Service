package com.bhushan.authservice.authservice.service;

import com.bhushan.authservice.authservice.entity.Role;
import com.bhushan.authservice.authservice.entity.User;
import com.bhushan.authservice.authservice.entity.UserProfile;
import com.bhushan.authservice.authservice.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CustomUserDetailsService implements UserDetailsService {

 private final UserRepo userRepo;


 @Autowired
 public  CustomUserDetailsService(UserRepo userRepo)
 {
     this.userRepo=userRepo;
 }


 @Override
 @Cacheable(value = "users",key = "#email")
 public CustomUser loadUserByUsername(String email) throws UsernameNotFoundException
 {


     User user= userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
     Set<String> roles= user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet());

     return new CustomUser(user.getEmail(), user.getPassword(),roles);

 }

}
