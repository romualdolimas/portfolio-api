package com.portfolio.service;

import com.portfolio.dto.ProjetoRequest;
import com.portfolio.dto.ProjetoResponse;
import com.portfolio.exception.RecursoNaoEncontradoException;
import com.portfolio.exception.RegraNegocioException;
import com.portfolio.model.AtribuicaoMembro;
import com.portfolio.model.Membro;
import com.portfolio.model.NivelRisco;
import com.portfolio.model.Projeto;
import com.portfolio.model.StatusProjeto;
import com.portfolio.repository.MembroRepository;
import com.portfolio.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private static final BigDecimal LIMITE_RISCO_BAIXO =
            new BigDecimal("100000.00");

    private static final BigDecimal LIMITE_RISCO_MEDIO =
            new BigDecimal("500000.00");

    private static final Map<StatusProjeto, StatusProjeto> PROXIMO_STATUS =
            Map.of(
                    StatusProjeto.EM_ANALISE,
                    StatusProjeto.ANALISE_REALIZADA,
                    StatusProjeto.ANALISE_REALIZADA,
                    StatusProjeto.ANALISE_APROVADA,
                    StatusProjeto.ANALISE_APROVADA,
                    StatusProjeto.INICIADO,
                    StatusProjeto.INICIADO,
                    StatusProjeto.PLANEJADO,
                    StatusProjeto.PLANEJADO,
                    StatusProjeto.EM_ANDAMENTO,
                    StatusProjeto.EM_ANDAMENTO,
                    StatusProjeto.ENCERRADO
            );

    private final ProjetoRepository projetoRepository;
    private final MembroRepository membroRepository;

    @Transactional
    public ProjetoResponse criar(ProjetoRequest request) {
        validarDatas(request.dataInicio(), request.previsaoTermino());
        validarQuantidadeMembros(request.membroIds());
        validarStatusInicial(request.statusAtual());

        Membro gerenteResponsavel = buscarMembroPorId(
                request.gerenteResponsavelId()
        );
        Set<Membro> membros = buscarEValidarMembros(request.membroIds());

        Projeto projeto = new Projeto();
        projeto.setNome(request.nome());
        projeto.setDataInicio(request.dataInicio());
        projeto.setPrevisaoTermino(request.previsaoTermino());
        projeto.setDataRealTermino(request.dataRealTermino());
        projeto.setOrcamentoTotal(request.orcamentoTotal());
        projeto.setDescricao(request.descricao());
        projeto.setGerenteResponsavel(gerenteResponsavel);
        projeto.setStatusAtual(request.statusAtual());
        projeto.setMembros(membros);

        Projeto projetoSalvo = projetoRepository.save(projeto);

        return converterParaResponse(projetoSalvo);
    }

    @Transactional
    public ProjetoResponse atualizar(Long id, ProjetoRequest request) {
        Projeto projeto = buscarEntidadePorId(id);

        validarDatas(request.dataInicio(), request.previsaoTermino());
        validarQuantidadeMembros(request.membroIds());
        validarTransicaoStatus(
                projeto.getStatusAtual(),
                request.statusAtual()
        );

        Membro gerenteResponsavel = buscarMembroPorId(
                request.gerenteResponsavelId()
        );
        Set<Membro> membros = buscarEValidarMembrosParaAtualizacao(
                request.membroIds(),
                projeto.getId()
        );

        projeto.setNome(request.nome());
        projeto.setDataInicio(request.dataInicio());
        projeto.setPrevisaoTermino(request.previsaoTermino());
        projeto.setDataRealTermino(request.dataRealTermino());
        projeto.setOrcamentoTotal(request.orcamentoTotal());
        projeto.setDescricao(request.descricao());
        projeto.setGerenteResponsavel(gerenteResponsavel);
        projeto.setStatusAtual(request.statusAtual());
        projeto.setMembros(membros);

        Projeto projetoAtualizado = projetoRepository.save(projeto);

        return converterParaResponse(projetoAtualizado);
    }

    @Transactional
    public void excluir(Long id) {
        Projeto projeto = buscarEntidadePorId(id);

        validarExclusao(projeto);

        projetoRepository.delete(projeto);
    }

    @Transactional(readOnly = true)
    public ProjetoResponse buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<ProjetoResponse> listar(
            String nome,
            StatusProjeto status,
            Pageable pageable) {

        String nomeFiltrado =
                nome == null || nome.isBlank() ? null : nome.trim();

        return projetoRepository
                .listarComFiltros(nomeFiltrado, status, pageable)
                .map(this::converterParaResponse);
    }

    @Transactional(readOnly = true)
    public Projeto buscarEntidadePorId(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Projeto não encontrado teste"
                ));
    }

    public NivelRisco calcularRisco(
            BigDecimal orcamento,
            LocalDate dataInicio,
            LocalDate previsaoTermino) {

        validarDatas(dataInicio, previsaoTermino);

        LocalDate limiteTresMeses = dataInicio.plusMonths(3);
        LocalDate limiteSeisMeses = dataInicio.plusMonths(6);

        if (orcamento.compareTo(LIMITE_RISCO_MEDIO) > 0
                || previsaoTermino.isAfter(limiteSeisMeses)) {
            return NivelRisco.ALTO;
        }

        if (orcamento.compareTo(LIMITE_RISCO_BAIXO) > 0
                || previsaoTermino.isAfter(limiteTresMeses)) {
            return NivelRisco.MEDIO;
        }

        return NivelRisco.BAIXO;
    }

    private Membro buscarMembroPorId(Long id) {
        return membroRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Membro não encontrado"
                ));
    }

    private Set<Membro> buscarEValidarMembros(Set<Long> membroIds) {
        List<Membro> membrosEncontrados =
                membroRepository.buscarTodosPorIds(membroIds);

        validarMembrosEncontrados(membroIds, membrosEncontrados);

        for (Membro membro : membrosEncontrados) {
            validarAtribuicaoMembro(membro);
            validarQuantidadeProjetosAtivos(membro);
        }

        return new HashSet<>(membrosEncontrados);
    }

    private Set<Membro> buscarEValidarMembrosParaAtualizacao(
            Set<Long> membroIds,
            Long projetoId) {

        List<Membro> membrosEncontrados =
                membroRepository.buscarTodosPorIds(membroIds);

        validarMembrosEncontrados(membroIds, membrosEncontrados);

        for (Membro membro : membrosEncontrados) {
            validarAtribuicaoMembro(membro);
            validarQuantidadeProjetosAtivosExcetoProjeto(
                    membro,
                    projetoId
            );
        }

        return new HashSet<>(membrosEncontrados);
    }

    private void validarMembrosEncontrados(
            Set<Long> membroIds,
            List<Membro> membrosEncontrados) {

        if (membrosEncontrados.size() != membroIds.size()) {
            throw new RecursoNaoEncontradoException(
                    "Um ou mais membros não foram encontrados"
            );
        }
    }

    private void validarAtribuicaoMembro(Membro membro) {
        if (!AtribuicaoMembro.FUNCIONARIO.equalsIgnoreCase(
                membro.getAtribuicao())) {
            throw new RegraNegocioException(
                    "Somente funcionários podem ser associados ao projeto"
            );
        }
    }

    private void validarQuantidadeProjetosAtivos(Membro membro) {
        long quantidadeProjetosAtivos =
                projetoRepository.contarProjetosAtivosPorMembro(
                        membro.getId(),
                        List.of(StatusProjeto.ENCERRADO, StatusProjeto.CANCELADO)
                );

        validarLimiteProjetosAtivos(quantidadeProjetosAtivos);
    }

    private void validarQuantidadeProjetosAtivosExcetoProjeto(
            Membro membro,
            Long projetoId) {

        long quantidadeProjetosAtivos =
                projetoRepository.contarProjetosAtivosPorMembroExcetoProjeto(
                        membro.getId(),
                        projetoId,
                        List.of(StatusProjeto.ENCERRADO, StatusProjeto.CANCELADO)
                );

        validarLimiteProjetosAtivos(quantidadeProjetosAtivos);
    }

    private void validarLimiteProjetosAtivos(long quantidadeProjetosAtivos) {
        if (quantidadeProjetosAtivos >= 3) {
            throw new RegraNegocioException(
                    "O membro já está associado a três projetos ativos"
            );
        }
    }

    private void validarQuantidadeMembros(Set<Long> membroIds) {
        if (membroIds == null || membroIds.isEmpty() || membroIds.size() > 10) {
            throw new RegraNegocioException(
                    "O projeto deve possuir entre 1 e 10 membros"
            );
        }
    }

    private void validarStatusInicial(StatusProjeto status) {
        if (status != StatusProjeto.EM_ANALISE) {
            throw new RegraNegocioException(
                    "O projeto deve iniciar com o status EM_ANALISE"
            );
        }
    }

    private void validarTransicaoStatus(
            StatusProjeto statusAtual,
            StatusProjeto novoStatus) {

        if (novoStatus == statusAtual
                || novoStatus == StatusProjeto.CANCELADO) {
            return;
        }

        StatusProjeto proximoStatusPermitido =
                PROXIMO_STATUS.get(statusAtual);

        if (proximoStatusPermitido != novoStatus) {
            throw new RegraNegocioException(
                    "Transição de status não permitida"
            );
        }
    }

    private ProjetoResponse converterParaResponse(Projeto projeto) {
        ProjetoResponse response = new ProjetoResponse();
        response.setId(projeto.getId());
        response.setNome(projeto.getNome());
        response.setDataInicio(projeto.getDataInicio());
        response.setPrevisaoTermino(projeto.getPrevisaoTermino());
        response.setDataRealTermino(projeto.getDataRealTermino());
        response.setOrcamentoTotal(projeto.getOrcamentoTotal());
        response.setDescricao(projeto.getDescricao());
        response.setGerenteResponsavelId(
                projeto.getGerenteResponsavel().getId()
        );
        response.setGerenteResponsavelNome(
                projeto.getGerenteResponsavel().getNome()
        );
        response.setStatusAtual(projeto.getStatusAtual());
        response.setNivelRisco(calcularRisco(
                projeto.getOrcamentoTotal(),
                projeto.getDataInicio(),
                projeto.getPrevisaoTermino()
        ));
        response.setMembrosIds(projeto.getMembros().stream()
                .map(Membro::getId)
                .collect(Collectors.toSet()));

        return response;
    }

    private void validarDatas(
            LocalDate dataInicio,
            LocalDate previsaoTermino) {

        if (previsaoTermino.isBefore(dataInicio)) {
            throw new RegraNegocioException(
                    "A previsão de término não pode ser anterior à data de início"
            );
        }
    }

    private void validarExclusao(Projeto projeto) {
        StatusProjeto status = projeto.getStatusAtual();

        if (status == StatusProjeto.INICIADO
                || status == StatusProjeto.EM_ANDAMENTO
                || status == StatusProjeto.ENCERRADO) {
            throw new RegraNegocioException(
                    "O projeto não pode ser excluído no status atual"
            );
        }
    }
}