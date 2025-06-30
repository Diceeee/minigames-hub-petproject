package com.dice.auth.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationDto {

    @Email
    private String email;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
