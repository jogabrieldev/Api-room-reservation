package com.ApiRoomRerservation.service;

import com.ApiRoomRerservation.dto.CadastrarUsuarioRequest;
import com.ApiRoomRerservation.dto.UsuarioResponse;
import com.ApiRoomRerservation.entity.Usuario;
import com.ApiRoomRerservation.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastrarUsuarioRequest request) {
        validar(request);

        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail");
        }

        Usuario usuario = new Usuario(
                request.nome().trim(),
                email,
                request.telefone().trim()
        );

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    private void validar(CadastrarUsuarioRequest request) {
        if (request == null
                || isBlank(request.nome())
                || isBlank(request.email())
                || isBlank(request.telefone())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome, e-mail e telefone são obrigatórios"
            );
        }

        if (!request.email().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail inválido");
        }

        if (request.telefone().trim().length() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O telefone deve ter no máximo 12 caracteres");
        }
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}
