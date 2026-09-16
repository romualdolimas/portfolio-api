package com.portfolio.dto;

import com.portfolio.model.StatusProjeto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class RelatorioResponse {

    private Map<StatusProjeto, Long> quantidadeProjetosPorStatus;

    private Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus;

    private Double mediaDuracaoProjetosEncerradosEmDias;

    private Long totalMembrosUnicosAlocados;
}