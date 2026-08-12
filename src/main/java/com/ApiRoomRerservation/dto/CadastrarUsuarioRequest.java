package com.ApiRoomRerservation.dto;

public record CadastrarUsuarioRequest(
        String nome,
        String email,
        String telefone
) {
}
