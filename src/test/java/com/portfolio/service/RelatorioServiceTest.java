package com.portfolio.service;

import com.portfolio.dto.RelatorioResponse;
import com.portfolio.model.Projeto;
import com.portfolio.model.StatusProjeto;
import com.portfolio.repository.ProjetoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import com.portfolio.model.Membro;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @Test
    void deveGerarQuantidadeEOrcamentoPorStatus() {
        // Arrange
        Projeto projetoEmAnalise = new Projeto();
        projetoEmAnalise.setStatusAtual(StatusProjeto.EM_ANALISE);
        projetoEmAnalise.setOrcamentoTotal(new BigDecimal("1000.00"));

        Projeto projetoEmAndamento = new Projeto();
        projetoEmAndamento.setStatusAtual(StatusProjeto.EM_ANDAMENTO);
        projetoEmAndamento.setOrcamentoTotal(new BigDecimal("2500.00"));

        when(projetoRepository.findAll())
                .thenReturn(List.of(projetoEmAnalise, projetoEmAndamento));

        // Act
        RelatorioResponse resultado = relatorioService.gerar();

        // Assert
        assertEquals(
                1L,
                resultado.getQuantidadeProjetosPorStatus()
                        .get(StatusProjeto.EM_ANALISE)
        );

        assertEquals(
                1L,
                resultado.getQuantidadeProjetosPorStatus()
                        .get(StatusProjeto.EM_ANDAMENTO)
        );

        assertEquals(
                new BigDecimal("1000.00"),
                resultado.getTotalOrcadoPorStatus()
                        .get(StatusProjeto.EM_ANALISE)
        );

        assertEquals(
                new BigDecimal("2500.00"),
                resultado.getTotalOrcadoPorStatus()
                        .get(StatusProjeto.EM_ANDAMENTO)
        );

        verify(projetoRepository).findAll();
    }

    @Test
    void deveCalcularMediaDeDuracaoDosProjetosEncerrados() {
        // Arrange
        Projeto primeiroProjeto = new Projeto();
        primeiroProjeto.setStatusAtual(StatusProjeto.ENCERRADO);
        primeiroProjeto.setDataInicio(LocalDate.of(2026, 1, 1));
        primeiroProjeto.setDataRealTermino(LocalDate.of(2026, 1, 11));
        primeiroProjeto.setOrcamentoTotal(BigDecimal.ZERO);

        Projeto segundoProjeto = new Projeto();
        segundoProjeto.setStatusAtual(StatusProjeto.ENCERRADO);
        segundoProjeto.setDataInicio(LocalDate.of(2026, 2, 1));
        segundoProjeto.setDataRealTermino(LocalDate.of(2026, 2, 21));
        segundoProjeto.setOrcamentoTotal(BigDecimal.ZERO);

        when(projetoRepository.findAll())
                .thenReturn(List.of(primeiroProjeto, segundoProjeto));

        // Act
        RelatorioResponse resultado = relatorioService.gerar();

        // Assert
        assertEquals(
                15.0,
                resultado.getMediaDuracaoProjetosEncerradosEmDias()
        );

        verify(projetoRepository).findAll();
    }

    @Test
    void deveContarApenasMembrosUnicos() {
        // Arrange
        Membro maria = new Membro();
        maria.setId(1L);
        maria.setNome("Maria");
        maria.setAtribuicao("FUNCIONARIO");

        Membro joao = new Membro();
        joao.setId(2L);
        joao.setNome("João");
        joao.setAtribuicao("FUNCIONARIO");

        Projeto primeiroProjeto = new Projeto();
        primeiroProjeto.setStatusAtual(StatusProjeto.EM_ANDAMENTO);
        primeiroProjeto.setOrcamentoTotal(BigDecimal.ZERO);
        primeiroProjeto.setMembros(Set.of(maria, joao));

        Projeto segundoProjeto = new Projeto();
        segundoProjeto.setStatusAtual(StatusProjeto.PLANEJADO);
        segundoProjeto.setOrcamentoTotal(BigDecimal.ZERO);
        segundoProjeto.setMembros(Set.of(maria));

        when(projetoRepository.findAll())
                .thenReturn(List.of(primeiroProjeto, segundoProjeto));

        // Act
        RelatorioResponse resultado = relatorioService.gerar();

        // Assert
        assertEquals(
                2L,
                resultado.getTotalMembrosUnicosAlocados()
        );

        verify(projetoRepository).findAll();
    }
}