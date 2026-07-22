package ru.dxunvrs.xray_subscription.dto;

import ru.dxunvrs.xray_subscription.entity.UserEntity;

public record UserResponse(
        Long id,
        String email,
        String uuid
) {
    public static UserResponse fromEntity(UserEntity entity) {
        return new UserResponse(entity.getId(),
                entity.getEmail(),
                entity.getUuid());
    }
}
