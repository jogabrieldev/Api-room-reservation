package com.ApiRoomRerservation.controller;

import com.ApiRoomRerservation.dto.CadastrarUsuarioRequest;
import com.ApiRoomRerservation.dto.ReservaResponse;
import com.ApiRoomRerservation.dto.UsuarioResponse;
import com.ApiRoomRerservation.entity.StatusReserva;
import com.ApiRoomRerservation.service.ReservaService;
import com.ApiRoomRerservation.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Cadastro e consulta de usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final ReservaService reservaService;

    public UsuarioController(UsuarioService usuarioService, ReservaService reservaService) {
        this.usuarioService = usuarioService;
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar usuário")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Usuário cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")})
    public UsuarioResponse cadastrar(@Valid @RequestBody CadastrarUsuarioRequest request) {
        return usuarioService.cadastrar(request);
    }

    @GetMapping
    @Operation(summary = "Listar usuários")
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public UsuarioResponse buscarPorId(@PathVariable UUID id) {
        return usuarioService.buscarPorId(id);
    }

    @GetMapping("/{usuarioId}/reservas")
    @Operation(summary = "Listar reservas de um usuário")
    public List<ReservaResponse> listarReservas(
            @PathVariable UUID usuarioId,
            @Parameter(description = "Filtro opcional por status") @RequestParam(required = false) StatusReserva status) {
        return reservaService.listarPorUsuario(usuarioId, status);
    }
}
