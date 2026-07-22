package ru.dxunvrs.xray_subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dxunvrs.xray_subscription.service.SubscriptionService;

@RestController
@RequestMapping("/sub")
@RequiredArgsConstructor
@Tag(name = "Подписка для впн-клиентов")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Получить подписку пользователя",
            description = "Отдает vless-ключи пользователя, можно вставить ссылку напрямую в впн-клиенты"
    )
    @GetMapping("/{email}")
    public ResponseEntity<String> getSubscription(@PathVariable String email) {
        String content = subscriptionService.getSubscriptionContent(email);

        return ResponseEntity.ok()
                .body(content);
    }
}
