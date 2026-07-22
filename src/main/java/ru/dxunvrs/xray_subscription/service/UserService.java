package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dxunvrs.xray_subscription.entity.UserEntity;
import ru.dxunvrs.xray_subscription.exception.UserAlreadyExistsException;
import ru.dxunvrs.xray_subscription.exception.UserNotFoundException;
import ru.dxunvrs.xray_subscription.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final XrayGrpcService xrayGrpcService;

    @Transactional
    public UserEntity createUser(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String userUuid = UUID.randomUUID().toString();

        UserEntity entity = UserEntity.builder()
                .email(email)
                .uuid(userUuid)
                .build();

        userRepository.save(entity);

        xrayGrpcService.addUser(email, userUuid);

        return entity;
    }

    @Transactional
    public void deleteUser(String email) {
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(entity);

        xrayGrpcService.removeUser(email);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
