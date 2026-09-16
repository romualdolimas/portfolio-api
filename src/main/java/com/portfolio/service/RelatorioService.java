package com.portfolio.service;

import com.portfolio.dto.RelatorioResponse;
import com.portfolio.model.Membro;
import com.portfolio.model.Projeto;
import com.portfolio.model.StatusProjeto;
import com.portfolio.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ProjetoRepository projetoRepository;

    @Transactional(readOnly = true)
    public RelatorioResponse gerar() {
        List<Projeto> projetos = projetoRepository.findAll();

        Map<StatusProjeto, Long> quantidadePorStatus =
                criarMapaQuantidade(projetos);

        Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus =
                criarMapaOrcamento(projetos);

        Double mediaDuracao =
                calcularMediaDuracaoProjetosEncerrados(projetos);

        Long totalMembrosUnicos =
                contarMembrosUnicos(projetos);

        RelatorioResponse relatorioResponse =
                new RelatorioResponse();

        relatorioResponse.setQuantidadeProjetosPorStatus(
                quantidadePorStatus
        );
        relatorioResponse.setTotalOrcadoPorStatus(
                totalOrcadoPorStatus
        );
        relatorioResponse.setMediaDuracaoProjetosEncerradosEmDias(
                mediaDuracao
        );
        relatorioResponse.setTotalMembrosUnicosAlocados(
                totalMembrosUnicos
        );

        return relatorioResponse;
    }

    private Map<StatusProjeto, Long> criarMapaQuantidade(
            List<Projeto> projetos) {

        Map<StatusProjeto, Long> resultado =
                new EnumMap<>(StatusProjeto.class);

        for (StatusProjeto status : StatusProjeto.values()) {
            resultado.put(status, 0L);
        }

        for (Projeto projeto : projetos) {
            resultado.merge(
                    projeto.getStatusAtual(),
                    1L,
                    Long::sum
            );
        }

        return resultado;
    }

    private Map<StatusProjeto, BigDecimal> criarMapaOrcamento(
            List<Projeto> projetos) {

        Map<StatusProjeto, BigDecimal> resultado =
                new EnumMap<>(StatusProjeto.class);

        for (StatusProjeto status : StatusProjeto.values()) {
            resultado.put(status, BigDecimal.ZERO);
        }

        for (Projeto projeto : projetos) {
            resultado.merge(
                    projeto.getStatusAtual(),
                    projeto.getOrcamentoTotal(),
                    BigDecimal::add
            );
        }

        return resultado;
    }

    private Double calcularMediaDuracaoProjetosEncerrados(
            List<Projeto> projetos) {

        return projetos.stream()
                .filter(projeto ->
                        projeto.getStatusAtual()
                                == StatusProjeto.ENCERRADO)
                .filter(projeto ->
                        projeto.getDataRealTermino() != null)
                .mapToLong(projeto -> ChronoUnit.DAYS.between(
                        projeto.getDataInicio(),
                        projeto.getDataRealTermino()
                ))
                .average()
                .orElse(0.0);
    }

    private Long contarMembrosUnicos(List<Projeto> projetos) {
        Set<Long> membroIds = projetos.stream()
                .flatMap(projeto ->
                        projeto.getMembros().stream())
                .map(Membro::getId)
                .collect(Collectors.toSet());

        return (long) membroIds.size();
    }
}