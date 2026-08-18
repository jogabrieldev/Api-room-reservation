package com.ApiRoomRerservation.controller;

import com.ApiRoomRerservation.dto.*;
import com.ApiRoomRerservation.service.ReservaService;
import com.ApiRoomRerservation.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/salas")
@Tag(name = "Salas", description = "Gerenciamento de salas e disponibilidade")
public class SalaController {
    private final SalaService salaService;
    private final ReservaService reservaService;

    public SalaController(SalaService salaService, ReservaService reservaService) {
        this.salaService = salaService;
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar sala")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Sala cadastrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Nome de sala já utilizado")})
    public SalaResponse cadastrar(@Valid @RequestBody CadastrarSalaRequest request) {
        return salaService.cadastrar(request);
    }

    @GetMapping
    @Operation(summary = "Listar salas")
    public List<SalaResponse> listar() {
        return salaService.listarTodas();
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Buscar salas disponíveis",
            description = "Retorna salas ativas sem reservas não canceladas que conflitem com o intervalo.")
    public List<SalaResponse> buscarDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return salaService.buscarDisponiveis(inicio, fim);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sala por ID")
    public SalaResponse buscarPorId(@PathVariable Long id) {
        return salaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sala")
    public SalaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarSalaRequest request) {
        return salaService.atualizar(id, request);
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar sala", description = "Desativa a sala sem excluir seu histórico de reservas.")
    public SalaResponse desativar(@PathVariable Long id) {
        return salaService.desativar(id);
    }

    @GetMapping("/{salaId}/reservas")
    @Operation(summary = "Listar reservas de uma sala")
    public List<ReservaResponse> listarReservas(@PathVariable Long salaId) {
        return reservaService.listarPorSala(salaId);
    }
}
