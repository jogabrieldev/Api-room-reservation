package com.ApiRoomRerservation.dto;

import com.ApiRoomRerservation.entity.Reserva;
import com.ApiRoomRerservation.entity.StatusReserva;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaResponse(
        Long id,
        UUID usuarioId,
        String usuarioNome,
        Long salaId,
        String salaNome,
        LocalDateTime inicio,
        LocalDateTime fim,
        StatusReserva status,
        LocalDateTime criadaEm
) {
    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getUsuario().getId(), reserva.getUsuario().getNome(),
                reserva.getSala().getId(), reserva.getSala().getNome(), reserva.getInicio(), reserva.getFim(),
                reserva.getStatus(), reserva.getCriadaEm());
    }
}
