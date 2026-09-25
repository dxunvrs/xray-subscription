package ru.dxunvrs.xray_subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dxunvrs.xray_subscription.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.uplink = u.uplink + :upDiff, u.downlink = u.downlink + :downDiff WHERE u.email = :email")
    void addStatsByEmail(@Param("email") String email,
                         @Param("upDiff") Long upDiff,
                         @Param("downDiff") Long downDiff);

    @Query("SELECT u.email FROM UserEntity u")
    List<String> findAllEmails();
}
