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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {
    private static final UUID USUARIO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long SALA_ID = 2L;
    private static final LocalDateTime BASE = LocalDateTime.of(2030, 8, 20, 14, 0);

    @Mock ReservaRepository reservaRepository;
    @Mock SalaRepository salaRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock Usuario usuario;
    @Mock Sala sala;
    private ReservaService service;

    @BeforeEach
    void setUp() {
        service = new ReservaService(reservaRepository, salaRepository, usuarioRepository);
    }

    @Test
    void deveCriarReservaValida() {
        prepararSalaEUsuarioAtivos();
        prepararDadosDaResposta();
        when(reservaRepository.existeConflito(SALA_ID, BASE, BASE.plusHours(1), StatusReserva.CANCELADA)).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservaResponse response = service.criar(request(BASE, BASE.plusHours(1)));

        assertEquals(StatusReserva.ATIVA, response.status());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deveFalharQuandoSalaNaoExiste() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salaRepository.buscarPorIdComBloqueio(SALA_ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.criar(request(BASE, BASE.plusHours(1))));
    }

    @Test
    void deveFalharQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.criar(request(BASE, BASE.plusHours(1))));
        verify(salaRepository, never()).buscarPorIdComBloqueio(any());
    }

    @Test
    void deveRejeitarFimIgualOuAnteriorAoInicio() {
        assertAll(
                () -> assertThrows(InvalidRequestException.class, () -> service.criar(request(BASE, BASE))),
                () -> assertThrows(InvalidRequestException.class, () -> service.criar(request(BASE, BASE.minusMinutes(1))))
        );
    }

    @Test
    void deveRejeitarHorarioPassado() {
        LocalDateTime passado = LocalDateTime.now().minusHours(2);
        assertThrows(InvalidRequestException.class, () -> service.criar(request(passado, passado.plusHours(1))));
    }

    @Test
    void deveRejeitarSalaInativa() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salaRepository.buscarPorIdComBloqueio(SALA_ID)).thenReturn(Optional.of(sala));
        when(sala.getAtiva()).thenReturn(false);
        assertThrows(BusinessException.class, () -> service.criar(request(BASE, BASE.plusHours(1))));
    }

    @Test
    void deveRejeitarConflitoCompleto() { assertConflito(BASE, BASE.plusHours(1)); }

    @Test
    void deveRejeitarSobreposicaoInicial() { assertConflito(BASE.minusMinutes(30), BASE.plusMinutes(30)); }

    @Test
    void deveRejeitarSobreposicaoFinal() { assertConflito(BASE.plusMinutes(30), BASE.plusMinutes(90)); }

    @Test
    void deveRejeitarReservaInterna() { assertConflito(BASE.plusMinutes(15), BASE.plusMinutes(45)); }

    @Test
    void devePermitirIntervaloImediatamenteAnterior() { assertPermitido(BASE.minusHours(1), BASE); }

    @Test
    void devePermitirIntervaloImediatamentePosterior() { assertPermitido(BASE.plusHours(1), BASE.plusHours(2)); }

    @Test
    void reservaCanceladaNaoDeveBloquearNovoIntervalo() {
        assertPermitido(BASE, BASE.plusHours(1));
        verify(reservaRepository).existeConflito(SALA_ID, BASE, BASE.plusHours(1), StatusReserva.CANCELADA);
    }

    private void assertConflito(LocalDateTime inicio, LocalDateTime fim) {
        prepararSalaEUsuarioAtivos();
        when(reservaRepository.existeConflito(SALA_ID, inicio, fim, StatusReserva.CANCELADA)).thenReturn(true);
        assertThrows(ReservaConflitoException.class, () -> service.criar(request(inicio, fim)));
        verify(reservaRepository, never()).save(any());
    }

    private void assertPermitido(LocalDateTime inicio, LocalDateTime fim) {
        prepararSalaEUsuarioAtivos();
        prepararDadosDaResposta();
        when(reservaRepository.existeConflito(SALA_ID, inicio, fim, StatusReserva.CANCELADA)).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertDoesNotThrow(() -> service.criar(request(inicio, fim)));
    }

    private void prepararSalaEUsuarioAtivos() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salaRepository.buscarPorIdComBloqueio(SALA_ID)).thenReturn(Optional.of(sala));
        when(sala.getId()).thenReturn(SALA_ID);
        when(sala.getAtiva()).thenReturn(true);
    }

    private void prepararDadosDaResposta() {
        when(sala.getNome()).thenReturn("Sala 01");
        when(usuario.getId()).thenReturn(USUARIO_ID);
        when(usuario.getNome()).thenReturn("João Gabriel");
    }

    private CriarReservaRequest request(LocalDateTime inicio, LocalDateTime fim) {
        return new CriarReservaRequest(USUARIO_ID, SALA_ID, inicio, fim);
    }
}
