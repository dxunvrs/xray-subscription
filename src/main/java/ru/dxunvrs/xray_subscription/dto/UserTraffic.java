package ru.dxunvrs.xray_subscription.dto;

public record UserTraffic(
        String formattedUplinkBytes,
        String formattedDownlinkBytes,
        String formattedTotalBytes,
        long uplinkBytes,
        long downlinkBytes,
        long totalBytes
) {}
