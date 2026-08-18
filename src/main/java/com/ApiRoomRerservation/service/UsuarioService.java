package com.ApiRoomRerservation.service;

import com.ApiRoomRerservation.dto.CadastrarUsuarioRequest;
import com.ApiRoomRerservation.dto.UsuarioResponse;
import com.ApiRoomRerservation.entity.Usuario;
import com.ApiRoomRerservation.exception.BusinessException;
import com.ApiRoomRerservation.exception.ResourceNotFoundException;
import com.ApiRoomRerservation.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponse cadastrar(CadastrarUsuarioRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Já existe um usuário com este e-mail.");
        }
        Usuario usuario = new Usuario(request.nome().trim(), email, request.telefone().trim());
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(UUID id) {
        return UsuarioResponse.from(buscarEntidade(id));
    }

    public Usuario buscarEntidade(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }
}
