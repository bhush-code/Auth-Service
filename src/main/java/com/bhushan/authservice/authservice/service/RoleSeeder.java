package com.bhushan.authservice.authservice.service;

import com.bhushan.authservice.authservice.entity.Role;
import com.bhushan.authservice.authservice.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationRunner{

        @Autowired
        private final RoleRepository roleRepository;

        @Override
        public void run(ApplicationArguments args) {

            if (!roleRepository.existsByRoleName("ROLE_USER")) {
                roleRepository.save(new Role(null, "ROLE_USER", "Default user"));
            }

            if (!roleRepository.existsByRoleName("ROLE_ADMIN")) {
                roleRepository.save(new Role(null, "ROLE_ADMIN", "Admin user"));
            }
        }
    }

