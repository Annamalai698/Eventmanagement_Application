package com.example.eventmanagement_backend.security;

import com.example.eventmanagement_backend.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter // Generates getters for all fields including id, email, password, authorities
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    // Custom Getters (provided by @Getter, but manually listing for clarity)
    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities; // Changed to final

    public UserDetailsImpl(Long id, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // The role conversion logic belongs here, where the 'user' object is available.
    public static UserDetailsImpl build(User user) {

        List<GrantedAuthority> authorities;

        if (user.getRole() != null && user.getRole().getName() != null) {
            authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(user.getRole().getName().name())
            );
        } else {
            // Default role if none assigned
            authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
    // Only one implementation of getAuthorities() is needed, returning the field.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Spring Security Standard Getters
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; } // Use email as the username

    // Optional flags, typically true
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}

