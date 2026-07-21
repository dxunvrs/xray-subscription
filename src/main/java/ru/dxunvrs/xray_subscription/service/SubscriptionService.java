package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dxunvrs.xray_subscription.entity.UserEntity;
import ru.dxunvrs.xray_subscription.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserRepository userRepository;
    private final VlessLinkBuilder vlessLinkBuilder;

    @Transactional(readOnly = true)
    public String getSubscriptionContent(String token) {
        UserEntity user = userRepository.findBySubscriptionToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Нет такого пользователя"));

        return vlessLinkBuilder.buildVlessLink(user.getUuid(), user.getEmail());
    }
}
