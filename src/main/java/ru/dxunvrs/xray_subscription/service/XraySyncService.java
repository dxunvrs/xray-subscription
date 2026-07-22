package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.dxunvrs.xray_subscription.entity.UserEntity;
import ru.dxunvrs.xray_subscription.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class XraySyncService {
    private final UserRepository userRepository;
    private final XrayGrpcService xrayGrpcService;

    @EventListener(ApplicationReadyEvent.class)
    public void syncUsersOnStartup() {
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            try {
                xrayGrpcService.addUser(user.getEmail(), user.getUuid());
            } catch (Exception ignored) {}
        }
    }
}
