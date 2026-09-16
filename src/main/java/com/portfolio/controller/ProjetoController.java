package com.portfolio.controller;

import com.portfolio.dto.ProjetoRequest;
import com.portfolio.dto.ProjetoResponse;
import com.portfolio.model.StatusProjeto;
import com.portfolio.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    @PostMapping
    public ResponseEntity<ProjetoResponse> criar(
            @Valid @RequestBody ProjetoRequest request) {

        ProjetoResponse projetoCriado =
                projetoService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projetoCriado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponse> buscarPorId(
            @PathVariable Long id) {

        ProjetoResponse projeto =
                projetoService.buscarPorId(id);

        return ResponseEntity.ok(projeto);
    }

    @GetMapping
    public ResponseEntity<Page<ProjetoResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) StatusProjeto status,
            @PageableDefault(size = 10, sort = "nome")
            Pageable pageable) {

        Page<ProjetoResponse> projetos =
                projetoService.listar(nome, status, pageable);

        return ResponseEntity.ok(projetos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProjetoRequest request) {

        ProjetoResponse projetoAtualizado =
                projetoService.atualizar(id, request);

        return ResponseEntity.ok(projetoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        projetoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}