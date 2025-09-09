package com.pitchplease.authservice.model.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a login request named {@link LoginRequest} containing the user's email and password.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

}
