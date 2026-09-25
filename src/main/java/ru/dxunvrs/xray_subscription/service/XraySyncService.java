package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

    @Scheduled(initialDelayString = "1m", fixedDelayString = "${xray.stats.sync-interval}")
    @Transactional
    public void syncUserStats() {
        List<String> userEmails = userRepository.findAllEmails();

        for (String email: userEmails) {
            try {
                long uplinkDiff = xrayGrpcService.getUserUplink(email, true);
                long downlinkDiff = xrayGrpcService.getUserDownlink(email, true);

                if (uplinkDiff > 0 || downlinkDiff > 0) {
                    userRepository.addStatsByEmail(email, uplinkDiff, downlinkDiff);
                }
            } catch (Exception e) {
                log.warn("Failed to sync user {}: {}", email, e.getMessage());
            }
        }
    }
}
