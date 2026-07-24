package ru.dxunvrs.xray_subscription.dto;

public record ServerSummary(
    UserSummary users,
    TrafficSummary traffic
) {
    public record UserSummary(
            int totalUsers
    ) {}
    public record TrafficSummary(
            String totalUplinkBytes,
            String totalDownlinkBytes,
            String totalBytes
    ) {}
}