package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dxunvrs.xray_subscription.entity.UserEntity;
import ru.dxunvrs.xray_subscription.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final XrayGrpcService xrayGrpcService;

    @Transactional
    public UserEntity createUser(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Такой пользователь уже есть");
        }

        String userUuid = UUID.randomUUID().toString();

        UserEntity entity = UserEntity.builder()
                .email(email)
                .uuid(userUuid)
                .build();

        xrayGrpcService.addUser(email, userUuid);

        return entity;
    }

    @Transactional
    public void deleteUser(String email) {
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Нет такого пользователя"));

        userRepository.delete(entity);

        xrayGrpcService.removeUser(email);
    }
}
