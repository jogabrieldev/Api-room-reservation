package com.ApiRoomRerservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastrarUsuarioRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 12) String telefone
) {
}
