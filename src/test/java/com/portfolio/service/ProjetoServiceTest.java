package com.portfolio.service;

import com.portfolio.dto.ProjetoRequest;
import com.portfolio.dto.ProjetoResponse;
import com.portfolio.exception.RecursoNaoEncontradoException;
import com.portfolio.exception.RegraNegocioException;
import com.portfolio.model.Membro;
import com.portfolio.model.NivelRisco;
import com.portfolio.model.Projeto;
import com.portfolio.model.StatusProjeto;
import com.portfolio.repository.MembroRepository;
import com.portfolio.repository.ProjetoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private MembroRepository membroRepository;

    @InjectMocks
    private ProjetoService projetoService;

    @Test
    void naoDeveExcluirProjetoIniciado() {
        Projeto projeto = new Projeto();
        projeto.setId(1L);
        projeto.setStatusAtual(StatusProjeto.INICIADO);

        when(projetoRepository.findById(1L))
                .thenReturn(Optional.of(projeto));

        assertThrows(
                RegraNegocioException.class,
                () -> projetoService.excluir(1L)
        );

        verify(projetoRepository, never()).delete(projeto);
    }

    @Test
    void deveExcluirProjetoEmAnalise() {
        Projeto projeto = new Projeto();
        projeto.setId(1L);
        projeto.setStatusAtual(StatusProjeto.EM_ANALISE);

        when(projetoRepository.findById(1L))
                .thenReturn(Optional.of(projeto));

        projetoService.excluir(1L);

        verify(projetoRepository).delete(projeto);
    }

    @Test
    void deveCalcularRiscoBaixo() {
        NivelRisco resultado = projetoService.calcularRisco(
                new BigDecimal("90000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 1)
        );

        assertEquals(NivelRisco.BAIXO, resultado);
    }

    @Test
    void deveConsiderarTresMesesComoRiscoBaixo() {
        NivelRisco resultado = projetoService.calcularRisco(
                new BigDecimal("100000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 4, 1)
        );

        assertEquals(NivelRisco.BAIXO, resultado);
    }

    @Test
    void deveCalcularRiscoMedio() {
        NivelRisco resultado = projetoService.calcularRisco(
                new BigDecimal("250000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 4, 1)
        );

        assertEquals(NivelRisco.MEDIO, resultado);
    }

    @Test
    void deveCalcularRiscoAlto() {
        NivelRisco resultado = projetoService.calcularRisco(
                new BigDecimal("600000.00"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 1)
        );

        assertEquals(NivelRisco.ALTO, resultado);
    }

    @Test
    void deveLancarErroQuandoProjetoNaoExistir() {
        when(projetoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> projetoService.buscarEntidadePorId(99L)
        );
    }

    @Test
    void deveRejeitarPrevisaoAnteriorADataInicio() {
        assertThrows(
                RegraNegocioException.class,
                () -> projetoService.calcularRisco(
                        new BigDecimal("50000.00"),
                        LocalDate.of(2026, 2, 1),
                        LocalDate.of(2026, 1, 1)
                )
        );
    }

    @Test
    void deveCriarProjetoComFuncionario() {
        Membro gerente = criarMembro(10L, "Gerente", "GERENTE");
        Membro funcionario = criarMembro(20L, "Funcionário", "FUNCIONARIO");
        ProjetoRequest request = criarRequest(Set.of(20L));

        when(membroRepository.buscarPorId(10L))
                .thenReturn(Optional.of(gerente));
        when(membroRepository.buscarTodosPorIds(Set.of(20L)))
                .thenReturn(List.of(funcionario));
        when(projetoRepository.contarProjetosAtivosPorMembro(
                20L,
                List.of(StatusProjeto.ENCERRADO, StatusProjeto.CANCELADO)
        )).thenReturn(0L);
        when(projetoRepository.save(any(Projeto.class)))
                .thenAnswer(invocacao -> {
                    Projeto projetoSalvo = invocacao.getArgument(0);
                    projetoSalvo.setId(1L);
                    return projetoSalvo;
                });

        ProjetoResponse resultado = projetoService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Projeto Teste", resultado.getNome());
        assertEquals(NivelRisco.BAIXO, resultado.getNivelRisco());
        assertEquals(Set.of(20L), resultado.getMembrosIds());
        verify(projetoRepository).save(any(Projeto.class));
    }

    @Test
    void naoDeveAssociarMembroNaoFuncionario() {
        Membro gerente = criarMembro(10L, "Gerente", "GERENTE");
        Membro membro = criarMembro(20L, "Consultor", "CONSULTOR");
        ProjetoRequest request = criarRequest(Set.of(20L));

        when(membroRepository.buscarPorId(10L))
                .thenReturn(Optional.of(gerente));
        when(membroRepository.buscarTodosPorIds(Set.of(20L)))
                .thenReturn(List.of(membro));

        assertThrows(
                RegraNegocioException.class,
                () -> projetoService.criar(request)
        );

        verify(projetoRepository, never()).save(any(Projeto.class));
    }

    @Test
    void naoDeveCriarProjetoSemMembros() {
        ProjetoRequest request = criarRequest(Set.of());

        assertThrows(
                RegraNegocioException.class,
                () -> projetoService.criar(request)
        );

        verify(projetoRepository, never()).save(any(Projeto.class));
    }

    @Test
    void naoDeveCriarProjetoComMaisDeDezMembros() {
        ProjetoRequest request = criarRequest(Set.of(
                1L, 2L, 3L, 4L, 5L, 6L,
                7L, 8L, 9L, 10L, 11L
        ));

        assertThrows(
                RegraNegocioException.class,
                () -> projetoService.criar(request)
        );

        verify(projetoRepository, never()).save(any(Projeto.class));
    }

    @Test
    void naoDeveAssociarMembroComTresProjetosAtivos() {
        Membro gerente = criarMembro(10L, "Gerente", "GERENTE");
        Membro funcionario = criarMembro(20L, "Funcionário", "FUNCIONARIO");
        ProjetoRequest request = criarRequest(Set.of(20L));

        when(membroRepository.buscarPorId(10L))
                .thenReturn(Optional.of(gerente));
        when(membroRepository.buscarTodosPorIds(Set.of(20L)))
                .thenReturn(List.of(funcionario));
        when(projetoRepository.contarProjetosAtivosPorMembro(
                20L,
                List.of(StatusProjeto.ENCERRADO, StatusProjeto.CANCELADO)
        )).thenReturn(3L);

        assertThrows(
                RegraNegocioException.class,
                () -> projetoService.criar(request)
        );

        verify(projetoRepository, never()).save(any(Projeto.class));
    }

    private ProjetoRequest criarRequest(Set<Long> membroIds) {
        return new ProjetoRequest(
                "Projeto Teste",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 1),
                null,
                new BigDecimal("50000.00"),
                "Descrição",
                10L,
                StatusProjeto.EM_ANALISE,
                membroIds
        );
    }

    private Membro criarMembro(Long id, String nome, String atribuicao) {
        Membro membro = new Membro();
        membro.setId(id);
        membro.setNome(nome);
        membro.setAtribuicao(atribuicao);
        return membro;
    }
}