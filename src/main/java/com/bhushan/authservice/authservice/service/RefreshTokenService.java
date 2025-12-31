package com.bhushan.authservice.authservice.service;


import com.bhushan.authservice.authservice.entity.RefreshToken;
import com.bhushan.authservice.authservice.entity.User;
import com.bhushan.authservice.authservice.exception.CustomRefreshTokenException;
import com.bhushan.authservice.authservice.exception.CustomUserException;
import com.bhushan.authservice.authservice.repo.RefreshTokenRepository;
import com.bhushan.authservice.authservice.repo.UserRepo;
import com.bhushan.authservice.authservice.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RefreshTokenService {

    @Value("${security.refresh.expiry-days}")
    private int expiryDays;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Autowired
    private UserRepo userRepo;


    @Autowired
    @Qualifier("refreshTokenEncoder")
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    @Transactional
    public String createRefreshToken(User user) {
        if (user == null || user.getUserId() == null) {
            throw new CustomUserException("User is invalid or null", HttpStatus.BAD_REQUEST);
        }

        long startTime=System.currentTimeMillis();
        refreshTokenRepository.deleteByUserUserId(user.getUserId());
        log.info("Deletion takes {} ms", (System.currentTimeMillis()-startTime));

        String rawToken = generateSecureToken();

        long startTime1=System.currentTimeMillis();
        String hashedToken = passwordEncoder.encode(rawToken);
        log.info("Hashing Refresh takes {} ms", (System.currentTimeMillis()-startTime1));

        RefreshToken newRefreshToken = new RefreshToken();

        newRefreshToken.setUser(user);
        newRefreshToken.setExpiryDate(Instant.now().plus(expiryDays, ChronoUnit.DAYS));
        newRefreshToken.setToken(hashedToken);
        refreshTokenRepository.save(newRefreshToken);
        return rawToken;
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Map<String,String> refresh(String rawToken, String expiredJwtToken)
    {
        expiredJwtToken=expiredJwtToken.replace("Bearer ","");

        String userIdFromExpiredJwt=jwtService.extractUserIdFromExpiredJwt(expiredJwtToken);

//        UUID userId=UUID.fromString(userIdFromExpiredJwt);
        Optional<User> user=userRepo.findByEmail(userIdFromExpiredJwt);

        if(user.isEmpty())
        {
            throw new CustomUserException("User not found",HttpStatus.NOT_FOUND);
        }


        RefreshToken rt=refreshTokenRepository.findByUserUserId(user.get().getUserId());

        if(!passwordEncoder.matches(rawToken,rt.getToken()))
        {
            throw new CustomRefreshTokenException("Invalid Refresh token",HttpStatus.BAD_REQUEST);
        }

        if(rt.getExpiryDate().isBefore(Instant.now()))
        {
            throw new CustomRefreshTokenException("Refresh Token is expired. Please log in again",HttpStatus.UNAUTHORIZED);

        }

        UserDetails currentAuthUser= (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String newAccessToken= jwtService.generateToken(currentAuthUser);


        return Map.of("accessToken",newAccessToken);


    }

}
