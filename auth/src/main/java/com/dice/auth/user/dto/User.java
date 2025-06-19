package com.dice.auth.user.dto;

import lombok.Builder;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class User implements UserDetails {

    Long id;
    String password;
    String email;
    String username;
    String nickname;
    boolean emailVerified;
    @Builder.Default
    List<String> authorities = new ArrayList<>();

    String googleId;
    String facebookId;
    String lastIssuedRefreshTokenId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }


    public boolean isRegistered() {
        return isEmailVerified()
                && getEmail() != null
                && getNickname() != null
                && getUsername() != null
                && getPassword() != null;
    }
}
