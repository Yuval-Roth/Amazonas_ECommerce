package com.amazonas.backend.business.authentication;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public final class UserCredentials implements UserDetails {
    @Id
    private final String userId;
    private final String password;

    public UserCredentials() {
        this.userId = null;
        this.password = null;
    }

    public UserCredentials(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    public String userId() {
        return userId;
    }

    public String password() {
        return password;
    }
}
