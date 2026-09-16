package com.portfolio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, Object>> tratarRegraNegocio(
            RegraNegocioException exception) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException exception) {

        return criarResposta(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException exception) {

        Map<String, String> erros = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(erro -> erros.putIfAbsent(
                        erro.getField(),
                        erro.getDefaultMessage()
                ));

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("dataHora", LocalDateTime.now());
        resposta.put("status", HttpStatus.BAD_REQUEST.value());
        resposta.put("erro", HttpStatus.BAD_REQUEST.getReasonPhrase());
        resposta.put("mensagem", "Existem campos inválidos");
        resposta.put("campos", erros);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarMensagemInvalida(
            HttpMessageNotReadableException exception) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "O corpo da requisição está inválido"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroInesperado(
            Exception exception) {

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado"
        );
    }

    private ResponseEntity<Map<String, Object>> criarResposta(
            HttpStatus status,
            String mensagem) {

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("dataHora", LocalDateTime.now());
        resposta.put("status", status.value());
        resposta.put("erro", status.getReasonPhrase());
        resposta.put("mensagem", mensagem);

        return ResponseEntity
                .status(status)
                .body(resposta);
    }
}