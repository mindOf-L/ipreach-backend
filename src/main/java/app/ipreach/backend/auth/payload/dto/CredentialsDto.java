package app.ipreach.backend.auth.payload.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record CredentialsDto(
    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    @NotEmpty(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$",
        message = "Password must be at least 8 characters and contain at least one uppercase letter, one number, and one special character"
    )
    String password) { }
