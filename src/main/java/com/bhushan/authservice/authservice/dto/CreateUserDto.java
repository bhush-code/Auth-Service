package com.bhushan.authservice.authservice.dto;

public record CreateUserDto(
        //Auth Data
        String email,
        String password,

        //Profile Data
        String firstName,
        String lastName,
        String mobileNumber) {}
