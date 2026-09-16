package com.portfolio.dto;

import com.portfolio.model.NivelRisco;
import com.portfolio.model.StatusProjeto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class ProjetoResponse {

    private Long id;

    private String nome;

    private LocalDate dataInicio;

    private LocalDate previsaoTermino;

    private LocalDate dataRealTermino;

    private BigDecimal orcamentoTotal;

    private String descricao;

    private Long gerenteResponsavelId;

    private String gerenteResponsavelNome;

    private StatusProjeto statusAtual;

    private NivelRisco nivelRisco;

    private Set<Long> membrosIds;
}