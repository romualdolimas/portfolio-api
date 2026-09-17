package com.portfolio.controller;

import com.portfolio.dto.MembroRequest;
import com.portfolio.dto.MembroResponse;
import com.portfolio.service.MembroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class MembroControllerTest {

    @Mock
    private MembroService membroService;

    @InjectMocks
    private MembroController membroController;

    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(membroController)
                .build();
    }

    @Test
    void deveCriarMembro() throws Exception {
        // Arrange
        MembroResponse resposta = new MembroResponse();
        resposta.setId(1L);
        resposta.setNome("Maria");
        resposta.setAtribuicao("FUNCIONARIO");

        when(membroService.criar(any(MembroRequest.class)))
                .thenReturn(resposta);

        String json = """
                {
                  "nome": "Maria",
                  "atribuicao": "FUNCIONARIO"
                }
                """;

        // Act e Assert
        mockMvc.perform(
                        post("/api/membros")
                                .contentType(APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.atribuicao")
                        .value("FUNCIONARIO"));

        verify(membroService)
                .criar(any(MembroRequest.class));
    }

    @Test
    void deveBuscarMembroPorId() throws Exception {
        // Arrange
        MembroResponse resposta = new MembroResponse();
        resposta.setId(1L);
        resposta.setNome("Maria");
        resposta.setAtribuicao("FUNCIONARIO");

        when(membroService.buscarPorId(1L))
                .thenReturn(resposta);

        // Act e Assert
        mockMvc.perform(get("/api/membros/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.atribuicao")
                        .value("FUNCIONARIO"));

        verify(membroService).buscarPorId(1L);
    }
}