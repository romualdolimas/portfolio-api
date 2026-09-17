package com.portfolio.controller;

import com.portfolio.dto.ProjetoRequest;
import com.portfolio.dto.ProjetoResponse;
import com.portfolio.model.NivelRisco;
import com.portfolio.model.StatusProjeto;
import com.portfolio.service.ProjetoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjetoControllerTest {

    @Mock
    private ProjetoService projetoService;

    @InjectMocks
    private ProjetoController projetoController;

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(projetoController)
                .build();
    }

    @Test
    void deveCriarProjeto() throws Exception {
        // Arrange
        ProjetoResponse resposta = criarResposta();

        when(projetoService.criar(any(ProjetoRequest.class)))
                .thenReturn(resposta);

        String json = """
                {
                  "nome": "Sistema de Portfolio",
                  "dataInicio": "2026-09-17",
                  "previsaoTermino": "2027-01-17",
                  "dataRealTermino": null,
                  "orcamentoTotal": 50000.00,
                  "descricao": "Projeto de gerenciamento",
                  "gerenteResponsavelId": 1,
                  "statusAtual": "EM_ANALISE",
                  "membroIds": [2]
                }
                """;

        // Act e Assert
        mockMvc.perform(
                        post("/api/projetos")
                                .contentType(APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome")
                        .value("Sistema de Portfolio"))
                .andExpect(jsonPath("$.statusAtual")
                        .value("EM_ANALISE"))
                .andExpect(jsonPath("$.nivelRisco")
                        .value("BAIXO"));

        verify(projetoService)
                .criar(any(ProjetoRequest.class));
    }

    @Test
    void deveBuscarProjetoPorId() throws Exception {
        // Arrange
        ProjetoResponse resposta = criarResposta();

        when(projetoService.buscarPorId(10L))
                .thenReturn(resposta);

        // Act e Assert
        mockMvc.perform(get("/api/projetos/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome")
                        .value("Sistema de Portfolio"))
                .andExpect(jsonPath("$.gerenteResponsavelId")
                        .value(1))
                .andExpect(jsonPath("$.membrosIds[0]")
                        .value(2));

        verify(projetoService).buscarPorId(10L);
    }

    private ProjetoResponse criarResposta() {
        ProjetoResponse resposta = new ProjetoResponse();
        resposta.setId(10L);
        resposta.setNome("Sistema de Portfolio");
        resposta.setDataInicio(LocalDate.of(2026, 9, 17));
        resposta.setPrevisaoTermino(LocalDate.of(2027, 1, 17));
        resposta.setOrcamentoTotal(new BigDecimal("50000.00"));
        resposta.setDescricao("Projeto de gerenciamento");
        resposta.setGerenteResponsavelId(1L);
        resposta.setGerenteResponsavelNome("Gerente");
        resposta.setStatusAtual(StatusProjeto.EM_ANALISE);
        resposta.setNivelRisco(NivelRisco.BAIXO);
        resposta.setMembrosIds(Set.of(2L));

        return resposta;
    }
}