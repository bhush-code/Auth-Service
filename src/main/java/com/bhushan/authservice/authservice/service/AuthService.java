package com.bhushan.authservice.authservice.service;

import com.bhushan.authservice.authservice.dto.LoginRequest;
import com.bhushan.authservice.authservice.entity.RefreshToken;
import com.bhushan.authservice.authservice.entity.User;
import com.bhushan.authservice.authservice.exception.CustomUserException;
import com.bhushan.authservice.authservice.repo.UserRepo;
import com.bhushan.authservice.authservice.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.hibernate.dialect.function.SumReturnTypeResolver;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepo userRepo;
//    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
        private static final Logger log=LogManager.getLogger(AuthService.class);

public Map<String,String> authenticate(LoginRequest loginRequest)
    {
        long startTimeforauth=System.currentTimeMillis();
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        log.info("AuthenticationTime = {} ms",(System.currentTimeMillis()-startTimeforauth));

        UserDetails user=(UserDetails) authentication.getPrincipal();
        Optional<User> appUser=userRepo.findByEmail(user.getUsername());

        if(appUser.isEmpty())
        {
            throw new CustomUserException("User not found..", HttpStatus.NOT_FOUND);
        }

        long startTime=System.currentTimeMillis();
        String accessToken=jwtService.generateToken(user);
        log.info("JWT generation time = {} ms", System.currentTimeMillis() - startTime);
        String refreshToken=refreshTokenService.createRefreshToken(appUser.get());
        return Map.of("accessToken",accessToken,"refreshToken",refreshToken);
    }
}

