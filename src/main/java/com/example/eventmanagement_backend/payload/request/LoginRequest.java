package com.example.eventmanagement_backend.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Schema(description = "User email")
    private String email;

    @NotBlank
    @Schema(description = "User password")
    private String password;
}
