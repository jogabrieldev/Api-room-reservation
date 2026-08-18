package com.ApiRoomRerservation.controller;

import com.ApiRoomRerservation.dto.CriarReservaRequest;
import com.ApiRoomRerservation.dto.ReservaResponse;
import com.ApiRoomRerservation.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Criação, consulta e cancelamento de reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Realizar uma nova reserva",
            description = "Cria uma reserva para um usuário e uma sala no intervalo informado. Rejeita salas inativas ou horários conflitantes.")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Reserva criada"),
            @ApiResponse(responseCode = "400", description = "Dados ou intervalo inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário ou sala não encontrados"),
            @ApiResponse(responseCode = "409", description = "Sala inativa ou horário indisponível")})
    public ReservaResponse criar(@Valid @RequestBody CriarReservaRequest request) {
        return reservaService.criar(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ReservaResponse buscarPorId(@PathVariable Long id) {
        return reservaService.buscarPorId(id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva", description = "Altera o status para CANCELADA e preserva o histórico.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
            @ApiResponse(responseCode = "409", description = "Reserva já cancelada")})
    public ReservaResponse cancelar(@PathVariable Long id) {
        return reservaService.cancelar(id);
    }
}
