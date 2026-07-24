package ru.dxunvrs.xray_subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dxunvrs.xray_subscription.dto.CreateUserRequest;
import ru.dxunvrs.xray_subscription.dto.UserResponse;
import ru.dxunvrs.xray_subscription.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Управление подписками")
@SecurityRequirement(name = "basicAuth")
public class AdminUserController {
    private final UserService userService;

    @Operation(
            summary = "Создать нового пользователя",
            description = "Принимает email пользователя, добавляет в бд и создает подписку"
    )
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.createUser(request.email());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }

    @Operation(summary = "Получить данные о всех пользователях")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получить данные о конкретном пользователе")
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.findUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Удаление пользователя по его email")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        userService.deleteUser(email);

        return ResponseEntity.noContent().build();
    }
}