package com.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MembroRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 150, message = "O nome deve possuir no máximo 150 caracteres")
    private String nome;

    @NotBlank(message = "A atribuição é obrigatória")
    @Size(max = 100, message = "A atribuição deve possuir no máximo 100 caracteres")
    private String atribuicao;
}