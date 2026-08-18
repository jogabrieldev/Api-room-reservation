package com.ApiRoomRerservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarReservaRequest(
        @NotNull @Schema(example = "550e8400-e29b-41d4-a716-446655440000") UUID usuarioId,
        @NotNull @Schema(example = "2") Long salaId,
        @NotNull @Schema(example = "2026-08-20T14:00:00") LocalDateTime inicio,
        @NotNull @Schema(example = "2026-08-20T15:00:00") LocalDateTime fim
) {
}
