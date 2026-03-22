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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
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

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private static final String USER_CACHE_NAME = "User";

    private final UserRepo userRepo;
    private final UserProfileRepo userProfileRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CacheManager cacheManager;

    @Autowired
    public UserService(UserRepo userRepo, UserProfileRepo userProfileRepo, UserMapper userMapper,
                      PasswordEncoder passwordEncoder, RoleRepository roleRepository, CacheManager cacheManager)
    {
        this.userRepo = userRepo;
        this.userProfileRepo = userProfileRepo;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @CacheEvict(value = USER_CACHE_NAME, allEntries = true)
    public String createUser(CreateUserDto createUserDto)  {
        if(userProfileRepo.existsByMobileNumber(createUserDto.mobileNumber()) || userRepo.existsByEmail(createUserDto.email()))
        {
            throw new CustomUserException("User already Exists...!!", HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.toUserEntity(createUserDto);
        user.setUserId(UUID.randomUUID());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(createUserDto.password()));
        UserProfile userProfile = userMapper.toUserProfileEntity(createUserDto);
        userProfile.setUser(user);
        userProfile.setCreatedAt(LocalDateTime.now());
        user.setUserProfile(userProfile);
        Role userRole = roleRepository.findByRoleName("ROLE_USER").orElseThrow(()-> new CustomRoleException("ROLE_USER not exists..", HttpStatus.BAD_REQUEST));
        user.getRoles().add(userRole);
        userRepo.save(user);
        logger.info("User created successfully with userId: {}", user.getUserId());
        return "User Created Successfully";

    }

    @Cacheable(value = USER_CACHE_NAME, key = "#userId")
    public UserResponseDto getUser(UUID userId)
    {
        logger.debug("Fetching user from database for userId: {}", userId);
        Optional<User> user = userRepo.findById(userId);
        if(user.isEmpty())
        {
            throw new CustomUserException("User not exists", HttpStatus.NOT_FOUND);
        }
        return userMapper.toDto(user.get());
    }

    /**
     * Fallback method that bypasses cache and fetches directly from database
     */
    public UserResponseDto getUserWithFallback(UUID userId)
    {
        try {
            // Try to get from cache first
            Cache cache = cacheManager.getCache(USER_CACHE_NAME);
            if (cache != null) {
                Cache.ValueWrapper wrapper = cache.get(userId);
                if (wrapper != null && wrapper.get() instanceof UserResponseDto) {
                    logger.debug("Retrieved user from cache for userId: {}", userId);
                    return (UserResponseDto) wrapper.get();
                }
            }
        } catch (Exception e) {
            logger.warn("Cache retrieval failed for userId: {}, falling back to database: {}", userId, e.getMessage());
        }

        // Fallback to database
        logger.info("Falling back to database for user lookup with userId: {}", userId);
        Optional<User> user = userRepo.findById(userId);
        if(user.isEmpty())
        {
            throw new CustomUserException("User not exists", HttpStatus.NOT_FOUND);
        }

        UserResponseDto userResponseDto = userMapper.toDto(user.get());

        // Try to cache the result
        try {
            Cache cache = cacheManager.getCache(USER_CACHE_NAME);
            if (cache != null) {
                cache.put(userId, userResponseDto);
                logger.debug("Cached user in cache for userId: {}", userId);
            }
        } catch (Exception e) {
            logger.warn("Failed to cache user: {}", e.getMessage());
            // Continue even if caching fails
        }

        return userResponseDto;
    }

    public List<UserResponseDto> getUsers()
    {
        logger.debug("Fetching all users from database");
        List<User> users = userRepo.findAll();
        return userMapper.toUserDtoList(users);
    }
}
