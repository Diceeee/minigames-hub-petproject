package com.dice.auth.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Principal (username or email) is required")
    private String principal;
    
    @NotBlank(message = "Password is required")
    private String password;
} 