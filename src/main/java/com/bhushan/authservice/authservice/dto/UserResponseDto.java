package com.bhushan.authservice.authservice.dto;

import java.io.Serializable;

public record UserResponseDto(
        String userId,
        String firstName,
        String lastName,
        String email,
        String mobileNumber,
        String createdAt
) implements Serializable {}
