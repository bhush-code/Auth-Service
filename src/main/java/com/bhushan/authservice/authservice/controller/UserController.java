package com.bhushan.authservice.authservice.controller;


import com.bhushan.authservice.authservice.dto.CreateUserDto;
import com.bhushan.authservice.authservice.dto.UserResponseDto;
import com.bhushan.authservice.authservice.entity.User;
import com.bhushan.authservice.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/v1")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService=userService;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody CreateUserDto createUserDto)
    {
        String res=userService.createUser(createUserDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }



    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getUsers()
    {
        List<UserResponseDto> users=userService.getUsers();
        return new ResponseEntity<>(users,HttpStatus.OK);
    }
}
