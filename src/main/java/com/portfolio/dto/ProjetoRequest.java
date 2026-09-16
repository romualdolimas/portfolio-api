package com.portfolio.dto;

import com.portfolio.model.StatusProjeto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record ProjetoRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve possuir no máximo 150 caracteres")
        String nome,

        @NotNull(message = "A data de início é obrigatória")
        LocalDate dataInicio,

        @NotNull(message = "A previsão de término é obrigatória")
        LocalDate previsaoTermino,

        LocalDate dataRealTermino,

        @NotNull(message = "O orçamento total é obrigatório")
        @PositiveOrZero(message = "O orçamento não pode ser negativo")
        BigDecimal orcamentoTotal,

        @Size(max = 1000, message = "A descrição deve possuir no máximo 1000 caracteres")
        String descricao,

        @NotNull(message = "O gerente responsável é obrigatório")
        Long gerenteResponsavelId,

        @NotNull(message = "O status é obrigatório")
        StatusProjeto statusAtual,

        @NotNull(message = "Os membros são obrigatórios")
        @Size(
                min = 1,
                max = 10,
                message = "O projeto deve possuir entre 1 e 10 membros"
        )
        Set<Long> membroIds
) {
}