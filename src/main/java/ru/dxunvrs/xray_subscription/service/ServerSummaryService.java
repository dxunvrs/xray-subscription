package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dxunvrs.xray_subscription.dto.ServerSummary;
import ru.dxunvrs.xray_subscription.dto.UserResponse;
import ru.dxunvrs.xray_subscription.util.DataSizeFormatter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServerSummaryService {
    private final UserService userService;
    private final DataSizeFormatter dataSizeFormatter;

    public ServerSummary getServerSummary() {
        List<UserResponse> users = userService.getAllUsers();
        long totalUplinkBytes = 0L;
        long totalDownlinkBytes = 0L;
        for (UserResponse user : users) {
            totalUplinkBytes += user.stats().uplinkBytes();
            totalDownlinkBytes += user.stats().downlinkBytes();
        }
        return new ServerSummary(
                new ServerSummary.UserSummary(
                        users.size()
                ),
                new ServerSummary.TrafficSummary(
                        dataSizeFormatter.formatBytes(totalUplinkBytes),
                        dataSizeFormatter.formatBytes(totalDownlinkBytes),
                        dataSizeFormatter.formatBytes(totalUplinkBytes + totalDownlinkBytes)
                )

        );
    }
}
