package com.ApiRoomRerservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AtualizarSalaRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 500) String descricao,
        @NotNull @Positive Integer capacidade,
        @Size(max = 150) String localizacao
) {
}
