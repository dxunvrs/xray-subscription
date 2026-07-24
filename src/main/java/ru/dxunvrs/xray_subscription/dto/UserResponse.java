package ru.dxunvrs.xray_subscription.dto;

public record UserResponse(
        Long id,
        String email,
        String uuid,
        UserTraffic stats
) {}
