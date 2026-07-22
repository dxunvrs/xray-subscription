package ru.dxunvrs.xray_subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dxunvrs.xray_subscription.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Optional<UserEntity> findBySubscriptionToken(String subscriptionToken);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
