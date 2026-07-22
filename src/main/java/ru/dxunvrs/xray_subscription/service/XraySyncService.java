package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.dxunvrs.xray_subscription.entity.UserEntity;
import ru.dxunvrs.xray_subscription.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class XraySyncService {
    private final UserRepository userRepository;
    private final XrayGrpcService xrayGrpcService;

    @EventListener(ApplicationReadyEvent.class)
    public void syncUsersOnStartup() {
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            try {
                xrayGrpcService.addUser(user.getEmail(), user.getUuid());
            } catch (Exception e) {
                log.warn("Sync failed");
            }
        }
    }
}
