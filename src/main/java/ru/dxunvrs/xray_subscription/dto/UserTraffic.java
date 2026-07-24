package ru.dxunvrs.xray_subscription.dto;

public record UserTraffic(
        long uplinkBytes,
        long downlinkBytes,
        long totalBytes
) {}
