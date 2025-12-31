package com.bhushan.authservice.authservice.service;

import com.bhushan.authservice.authservice.entity.Role;
import com.bhushan.authservice.authservice.entity.User;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public class CustomUser implements UserDetails {

    private String email;
    private String password;
    private Set<String> roles;

    public CustomUser() {};

    @Autowired
    public CustomUser(String email, String password, Set<String> roles)
    {
       this.email=email;
       this.password=password;
       this.roles=roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream().map(role->new SimpleGrantedAuthority(role)).toList();

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
