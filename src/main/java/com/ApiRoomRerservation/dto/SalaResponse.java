package com.ApiRoomRerservation.dto;

import com.ApiRoomRerservation.entity.Sala;

public record SalaResponse(
        Long id,
        String nome,
        String descricao,
        Integer capacidade,
        String localizacao,
        Boolean ativa
) {
    public static SalaResponse from(Sala sala) {
        return new SalaResponse(sala.getId(), sala.getNome(), sala.getDescricao(), sala.getCapacidade(),
                sala.getLocalizacao(), sala.getAtiva());
    }
}
