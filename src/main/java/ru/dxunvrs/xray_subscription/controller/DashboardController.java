package ru.dxunvrs.xray_subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dxunvrs.xray_subscription.dto.ServerSummary;
import ru.dxunvrs.xray_subscription.service.ServerSummaryService;

@RestController
@RequestMapping("/api/admin/summary")
@RequiredArgsConstructor
@Tag(name = "Статистика сервера")
@SecurityRequirement(name = "basicAuth")
public class DashboardController {
    private final ServerSummaryService serverSummaryService;

    @Operation(
            summary = "Получить статистику сервера",
            description = "Количество пользователей, информация о трафике"
    )
    @GetMapping
    public ResponseEntity<ServerSummary> getServerSummary() {
        return ResponseEntity.ok(serverSummaryService.getServerSummary());
    }
}
