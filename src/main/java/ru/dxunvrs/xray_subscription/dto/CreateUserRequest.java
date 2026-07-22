package ru.dxunvrs.xray_subscription.dto;


import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest (
        @NotBlank(message = "Email is required")
        String email
) {}
