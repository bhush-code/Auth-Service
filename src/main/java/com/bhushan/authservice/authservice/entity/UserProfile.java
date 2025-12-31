package com.bhushan.authservice.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@Table(name = "user_profiles")
@Entity
public class UserProfile {
    @Id
    private UUID userId;

    private String firstName;
    private String lastName;
    private String mobileNumber;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    @OneToOne
    @MapsId
    @JoinColumn(name="user_id")
    private User user;
}
