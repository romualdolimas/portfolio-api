package com.portfolio.service;

import com.portfolio.dto.MembroRequest;
import com.portfolio.dto.MembroResponse;
import com.portfolio.model.Membro;
import com.portfolio.repository.MembroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import com.portfolio.exception.RecursoNaoEncontradoException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MembroServiceTest {

    @Mock
    private MembroRepository membroRepository;

    @InjectMocks
    private MembroService membroService;

    private MembroRequest request;

    @BeforeEach
    void prepararDados() {
        request = new MembroRequest();
        request.setNome("Maria");
        request.setAtribuicao("FUNCIONARIO");
    }

    @Test
    void deveCriarMembro() {
        // Arrange
        Membro membroSalvo = new Membro();
        membroSalvo.setId(1L);
        membroSalvo.setNome("Maria");
        membroSalvo.setAtribuicao("FUNCIONARIO");

        when(membroRepository.save(any(Membro.class)))
                .thenReturn(membroSalvo);

        // Act
        MembroResponse resultado = membroService.criar(request);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Maria", resultado.getNome());
        assertEquals("FUNCIONARIO", resultado.getAtribuicao());

        verify(membroRepository).save(any(Membro.class));
    }

    @Test
    void deveBuscarMembroPorId() {
        // Arrange
        Membro membroEncontrado = new Membro();
        membroEncontrado.setId(1L);
        membroEncontrado.setNome("Maria");
        membroEncontrado.setAtribuicao("FUNCIONARIO");

        when(membroRepository.buscarPorId(1L))
                .thenReturn(Optional.of(membroEncontrado));

        // Act
        MembroResponse resultado = membroService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Maria", resultado.getNome());
        assertEquals("FUNCIONARIO", resultado.getAtribuicao());

        verify(membroRepository).buscarPorId(1L);
    }

    @Test
    void deveLancarExcecaoQuandoMembroNaoExistir() {
        // Arrange
        when(membroRepository.buscarPorId(99L))
                .thenReturn(Optional.empty());

        // Act e Assert
        RecursoNaoEncontradoException excecao = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> membroService.buscarPorId(99L)
        );

        assertEquals("Membro não encontrado", excecao.getMessage());

        verify(membroRepository).buscarPorId(99L);
    }
}