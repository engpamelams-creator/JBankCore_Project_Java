package br.com.jbank.core.modulos.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthDTOs() {
    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
    ) {}

    public record SignupRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password
    ) {}

    public record JwtResponse(
        String accessToken,
        String tokenType
    ) {
        public JwtResponse(String accessToken) {
            this(accessToken, "Bearer");
        }
    }
}
