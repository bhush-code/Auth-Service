package com.bhushan.authservice.authservice.service;

import com.bhushan.authservice.authservice.dto.CreateUserDto;
import com.bhushan.authservice.authservice.dto.UserResponseDto;
import com.bhushan.authservice.authservice.entity.Role;
import com.bhushan.authservice.authservice.entity.User;
import com.bhushan.authservice.authservice.entity.UserProfile;
import com.bhushan.authservice.authservice.exception.CustomRoleException;
import com.bhushan.authservice.authservice.exception.CustomUserException;
import com.bhushan.authservice.authservice.mapper.UserMapper;
import com.bhushan.authservice.authservice.repo.RoleRepository;
import com.bhushan.authservice.authservice.repo.UserProfileRepo;
import com.bhushan.authservice.authservice.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final UserProfileRepo userProfileRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepo userRepo, UserProfileRepo userProfileRepo,UserMapper userMapper,PasswordEncoder passwordEncoder, RoleRepository roleRepository)
    {
        this.userRepo=userRepo;
        this.userProfileRepo=userProfileRepo;
        this.userMapper=userMapper;
        this.passwordEncoder=passwordEncoder;
        this.roleRepository=roleRepository;
    }

    @Transactional
    public String createUser(CreateUserDto createUserDto)  {
        if(userProfileRepo.existsByMobileNumber(createUserDto.mobileNumber()) || userRepo.existsByEmail(createUserDto.email()))
        {
            throw new CustomUserException("User already Exists...!!", HttpStatus.BAD_REQUEST);
        }
        User user=userMapper.toUserEntity(createUserDto);
        user.setUserId(UUID.randomUUID());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(createUserDto.password()));
        UserProfile userProfile=userMapper.toUserProfileEntity(createUserDto);
//        userProfile.se(UUID.randomUUID());
        userProfile.setUser(user);
//        userProfile.setPassword(passwordEncoder.encode(createUserDto.password()));
        userProfile.setCreatedAt(LocalDateTime.now());
        user.setUserProfile(userProfile);
        Role userRole=roleRepository.findByRoleName("ROLE_USER").orElseThrow(()-> new CustomRoleException("ROLE_USER not exists..", HttpStatus.BAD_REQUEST));
        user.getRoles().add(userRole);
        userRepo.save(user);
        return "User Created Successfully";

    }


    @Cacheable(value ="User", key="#userId")
    public UserResponseDto getUser(UUID userId)
    {
        Optional<User> user=userRepo.findById(userId);
        if(user.isEmpty())
        {
                throw new CustomUserException("User not exists",HttpStatus.NOT_FOUND);
        }
        return userMapper.toDto(user.get());
    }


    public List<UserResponseDto> getUsers()
    {
        List<User> users=userRepo.findAll();
        return userMapper.toUserDtoList(users);
    }
}
