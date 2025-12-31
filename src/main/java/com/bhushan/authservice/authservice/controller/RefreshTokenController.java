package com.bhushan.authservice.authservice.controller;

import com.bhushan.authservice.authservice.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/refresh")
public class RefreshTokenController {

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping()
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String rawRefreshToken, @RequestHeader("Authorization") String expiredJwtToken)
    {
        return new ResponseEntity<>(refreshTokenService.refresh(rawRefreshToken,expiredJwtToken), HttpStatus.OK);
    }
}
