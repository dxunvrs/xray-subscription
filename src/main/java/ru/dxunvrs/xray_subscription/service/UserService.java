package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dxunvrs.xray_subscription.dto.UserResponse;
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
    public UserResponse createUser(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String userUuid = UUID.randomUUID().toString();

        UserEntity createdUser = userRepository.save(
                UserEntity.builder()
                        .email(email)
                        .uuid(userUuid)
                        .build()
        );

        xrayGrpcService.addUser(email, userUuid);

        return toUserDto(createdUser);
    }

    @Transactional
    public void deleteUser(String email) {
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(entity);

        xrayGrpcService.removeUser(email);
    }

    @Transactional
    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(this::toUserDto).toList();
    }

    @Transactional
    public UserResponse findUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toUserDto(userEntity);
    }

    private UserResponse toUserDto(UserEntity userEntity) {
        return new UserResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getUuid(),
                xrayGrpcService.getUserTraffic(userEntity.getEmail())
        );
    }
}