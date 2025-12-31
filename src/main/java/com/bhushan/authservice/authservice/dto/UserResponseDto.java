package com.bhushan.authservice.authservice.dto;

public record UserResponseDto(
        String userId,
        String firstName,
        String lastName,
        String email,
        String mobileNumber,
        String createdAt
) {}
