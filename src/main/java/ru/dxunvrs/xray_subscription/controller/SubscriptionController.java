package ru.dxunvrs.xray_subscription.controller;

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
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/{email}")
    public ResponseEntity<String> getSubscription(@PathVariable String email) {
        String content = subscriptionService.getSubscriptionContent(email);

        return ResponseEntity.ok()
                .body(content);
    }
}
