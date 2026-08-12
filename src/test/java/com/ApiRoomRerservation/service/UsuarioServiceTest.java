package com.ApiRoomRerservation.service;

import com.ApiRoomRerservation.dto.CadastrarUsuarioRequest;
import com.ApiRoomRerservation.dto.UsuarioResponse;
import com.ApiRoomRerservation.entity.Usuario;
import com.ApiRoomRerservation.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComUuid() {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest(
                "  João Silva  ",
                "  JOAO@EMAIL.COM  ",
                " 11999999999 "
        );
        UUID id = UUID.randomUUID();
        Usuario usuarioSalvo = org.mockito.Mockito.mock(Usuario.class);

        when(usuarioRepository.existsByEmailIgnoreCase("joao@email.com")).thenReturn(false);
        when(usuarioSalvo.getId()).thenReturn(id);
        when(usuarioSalvo.getNome()).thenReturn("João Silva");
        when(usuarioSalvo.getEmail()).thenReturn("joao@email.com");
        when(usuarioSalvo.getTelefone()).thenReturn("11999999999");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        UsuarioResponse response = usuarioService.cadastrar(request);

        assertEquals(id, response.id());
        assertEquals("João Silva", response.nome());
        assertEquals("joao@email.com", response.email());
        assertEquals("11999999999", response.telefone());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("João Silva", captor.getValue().getNome());
        assertEquals("joao@email.com", captor.getValue().getEmail());
        assertEquals("11999999999", captor.getValue().getTelefone());
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado() {
        CadastrarUsuarioRequest request = new CadastrarUsuarioRequest(
                "João Silva",
                "joao@email.com",
                "11999999999"
        );
        when(usuarioRepository.existsByEmailIgnoreCase("joao@email.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.cadastrar(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
