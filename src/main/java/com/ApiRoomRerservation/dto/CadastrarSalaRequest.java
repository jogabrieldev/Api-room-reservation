package com.ApiRoomRerservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastrarSalaRequest(
        @NotBlank @Size(max = 100)
        @Schema(example = "Sala de Reunião 01") String nome,
        @Size(max = 500)
        @Schema(example = "Sala para reuniões da equipe") String descricao,
        @NotNull @Positive
        @Schema(example = "10") Integer capacidade,
        @Size(max = 150)
        @Schema(example = "2º andar") String localizacao
) {
}
