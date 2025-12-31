package com.bhushan.authservice.authservice.controller;

import com.bhushan.authservice.authservice.dto.UserResponseDto;
import com.bhushan.authservice.authservice.entity.User;
import com.bhushan.authservice.authservice.service.UserService;
import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/protected/v1")
public class ProtectRouteController {

    @Autowired
    private  UserService userService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable UUID userId)
    {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }
}
