package com.ApiRoomRerservation.service;

import com.ApiRoomRerservation.dto.CriarReservaRequest;
import com.ApiRoomRerservation.dto.ReservaResponse;
import com.ApiRoomRerservation.entity.Reserva;
import com.ApiRoomRerservation.entity.Sala;
import com.ApiRoomRerservation.entity.StatusReserva;
import com.ApiRoomRerservation.entity.Usuario;
import com.ApiRoomRerservation.exception.BusinessException;
import com.ApiRoomRerservation.exception.InvalidRequestException;
import com.ApiRoomRerservation.exception.ResourceNotFoundException;
import com.ApiRoomRerservation.exception.ReservaConflitoException;
import com.ApiRoomRerservation.repository.ReservaRepository;
import com.ApiRoomRerservation.repository.SalaRepository;
import com.ApiRoomRerservation.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository, SalaRepository salaRepository,
                          UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ReservaResponse criar(CriarReservaRequest request) {
        validarIntervaloParaCriacao(request.inicio(), request.fim());
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        Sala sala = salaRepository.buscarPorIdComBloqueio(request.salaId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada."));
        if (!Boolean.TRUE.equals(sala.getAtiva())) {
            throw new BusinessException("A sala está inativa e não pode receber reservas.");
        }
        if (reservaRepository.existeConflito(sala.getId(), request.inicio(), request.fim(), StatusReserva.CANCELADA)) {
            throw new ReservaConflitoException("A sala já possui uma reserva neste horário.");
        }
        Reserva reserva = new Reserva("Reserva - " + sala.getNome(), request.inicio(), request.fim(), usuario, sala);
        reserva.setStatus(StatusReserva.ATIVA);
        return ReservaResponse.from(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(Long id) {
        return ReservaResponse.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorUsuario(UUID usuarioId, StatusReserva status) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
        List<Reserva> reservas = status == null ? reservaRepository.findByUsuarioIdOrderByInicioDesc(usuarioId)
                : reservaRepository.findByUsuarioIdAndStatusOrderByInicioDesc(usuarioId, status);
        return reservas.stream().map(ReservaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorSala(Long salaId) {
        if (!salaRepository.existsById(salaId)) {
            throw new ResourceNotFoundException("Sala não encontrada.");
        }
        return reservaRepository.findBySalaIdOrderByInicioDesc(salaId).stream().map(ReservaResponse::from).toList();
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = reservaRepository.buscarPorIdComBloqueio(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada."));
        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new BusinessException("A reserva já está cancelada.");
        }
        reserva.setStatus(StatusReserva.CANCELADA);
        return ReservaResponse.from(reserva);
    }

    private Reserva buscarEntidade(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada."));
    }

    private void validarIntervaloParaCriacao(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || !fim.isAfter(inicio)) {
            throw new InvalidRequestException("O horário final deve ser posterior ao horário inicial.");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("O horário inicial não pode estar no passado.");
        }
    }
}
