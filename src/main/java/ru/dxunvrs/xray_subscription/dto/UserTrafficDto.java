package ru.dxunvrs.xray_subscription.dto;

public record UserTrafficDto(
        String email,
        long uplinkBytes,
        long downlinkBytes,
        long totalBytes
) {}
