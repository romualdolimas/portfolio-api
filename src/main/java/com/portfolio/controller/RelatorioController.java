package com.portfolio.controller;

import com.portfolio.dto.RelatorioResponse;
import com.portfolio.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/portfolio")
    public ResponseEntity<RelatorioResponse> gerarRelatorio() {
        RelatorioResponse relatorio =
                relatorioService.gerar();

        return ResponseEntity.ok(relatorio);
    }
}