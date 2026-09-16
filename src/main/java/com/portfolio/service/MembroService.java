package com.portfolio.service;

import com.portfolio.dto.MembroRequest;
import com.portfolio.dto.MembroResponse;
import com.portfolio.exception.RecursoNaoEncontradoException;
import com.portfolio.model.Membro;
import com.portfolio.repository.MembroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MembroService {

    private final MembroRepository membroRepository;

    @Transactional
    public MembroResponse criar(MembroRequest request) {
        Membro membro = new Membro();
        membro.setNome(request.getNome());
        membro.setAtribuicao(request.getAtribuicao());

        Membro membroSalvo = membroRepository.save(membro);

        return converterParaResponse(membroSalvo);
    }

    @Transactional(readOnly = true)
    public MembroResponse buscarPorId(Long id) {
        Membro membro = buscarEntidadePorId(id);

        return converterParaResponse(membro);
    }

    @Transactional(readOnly = true)
    public Membro buscarEntidadePorId(Long id) {
        return membroRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Membro não encontrado"
                ));
    }

    private MembroResponse converterParaResponse(Membro membro) {
        MembroResponse membroResponse = new MembroResponse();
        membroResponse.setId(membro.getId());
        membroResponse.setNome(membro.getNome());
        membroResponse.setAtribuicao(membro.getAtribuicao());

        return membroResponse;
    }
}