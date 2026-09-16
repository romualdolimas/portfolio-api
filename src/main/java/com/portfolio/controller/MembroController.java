package com.portfolio.controller;

import com.portfolio.dto.MembroRequest;
import com.portfolio.dto.MembroResponse;
import com.portfolio.service.MembroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membros")
@RequiredArgsConstructor
public class MembroController {

    private final MembroService membroService;

    @PostMapping
    public ResponseEntity<MembroResponse> criar(
            @Valid @RequestBody MembroRequest request) {

        MembroResponse membroCriado =
                membroService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(membroCriado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembroResponse> buscarPorId(
            @PathVariable Long id) {

        MembroResponse membro =
                membroService.buscarPorId(id);

        return ResponseEntity.ok(membro);
    }
}