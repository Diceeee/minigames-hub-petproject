package com.dice.auth.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Value
@Builder(toBuilder = true)
@JsonIgnoreProperties(value = {"password", "googleId", "facebookId", "accountNonLocked", "accountNonExpired", "credentialsNonExpired", "enabled", "createdAt", "id"})
public class User implements UserDetails {

    Long id;
    @ToString.Exclude
    String password;
    String email;
    String username;
    boolean emailVerified;
    Instant createdAt;
    @Singular
    List<String> authorities;

    String googleId;
    String facebookId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }


    public boolean isRegistered() {
        return isEmailVerified()
                && getEmail() != null
                && getUsername() != null
                && getPassword() != null;
    }

    public boolean inEmailVerificationProcess() {
        return getEmail() != null && getUsername() != null && getPassword() != null && !isEmailVerified();
    }
}
