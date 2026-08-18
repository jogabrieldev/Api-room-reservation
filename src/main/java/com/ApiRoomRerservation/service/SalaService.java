package com.ApiRoomRerservation.service;

import com.ApiRoomRerservation.dto.AtualizarSalaRequest;
import com.ApiRoomRerservation.dto.CadastrarSalaRequest;
import com.ApiRoomRerservation.dto.SalaResponse;
import com.ApiRoomRerservation.entity.Sala;
import com.ApiRoomRerservation.entity.StatusReserva;
import com.ApiRoomRerservation.exception.BusinessException;
import com.ApiRoomRerservation.exception.InvalidRequestException;
import com.ApiRoomRerservation.exception.ResourceNotFoundException;
import com.ApiRoomRerservation.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalaService {
    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Transactional
    public SalaResponse cadastrar(CadastrarSalaRequest request) {
        String nome = request.nome().trim();
        validarNomeDuplicado(nome, null);
        Sala sala = new Sala(nome, normalizar(request.descricao()), request.capacidade(), normalizar(request.localizacao()));
        return SalaResponse.from(salaRepository.save(sala));
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> listarTodas() {
        return salaRepository.findAll().stream().map(SalaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SalaResponse buscarPorId(Long id) {
        return SalaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public SalaResponse atualizar(Long id, AtualizarSalaRequest request) {
        Sala sala = buscarEntidade(id);
        String nome = request.nome().trim();
        validarNomeDuplicado(nome, id);
        sala.setNome(nome);
        sala.setDescricao(normalizar(request.descricao()));
        sala.setCapacidade(request.capacidade());
        sala.setLocalizacao(normalizar(request.localizacao()));
        return SalaResponse.from(sala);
    }

    @Transactional
    public SalaResponse desativar(Long id) {
        Sala sala = buscarEntidade(id);
        if (!Boolean.TRUE.equals(sala.getAtiva())) {
            throw new BusinessException("A sala já está desativada.");
        }
        sala.setAtiva(false);
        return SalaResponse.from(sala);
    }

    @Transactional(readOnly = true)
    public List<SalaResponse> buscarDisponiveis(LocalDateTime inicio, LocalDateTime fim) {
        validarIntervalo(inicio, fim);
        return salaRepository.buscarDisponiveis(inicio, fim, StatusReserva.CANCELADA)
                .stream().map(SalaResponse::from).toList();
    }

    public Sala buscarEntidade(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada."));
    }

    private void validarNomeDuplicado(String nome, Long id) {
        boolean duplicado = id == null ? salaRepository.existsByNomeIgnoreCase(nome)
                : salaRepository.existsByNomeIgnoreCaseAndIdNot(nome, id);
        if (duplicado) {
            throw new BusinessException("Já existe uma sala com este nome.");
        }
    }

    private void validarIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || !fim.isAfter(inicio)) {
            throw new InvalidRequestException("O horário final deve ser posterior ao horário inicial.");
        }
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
