package com.bhushan.authservice.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "userProfile")
@Table(name = "users")
public class User {
    @Id
    private UUID userId;

    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean enabled=true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles=new HashSet<>();


    @OneToOne(mappedBy = "user" , cascade=CascadeType.ALL,fetch = FetchType.LAZY)
    private UserProfile userProfile;



}
