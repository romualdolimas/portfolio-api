package com.portfolio.repository;

import com.portfolio.model.Projeto;
import com.portfolio.model.StatusProjeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    @Query("""
            SELECT p
            FROM Projeto p
            WHERE (:nome IS NULL
                   OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:status IS NULL
                   OR p.statusAtual = :status)
            """)
    Page<Projeto> listarComFiltros(
            @Param("nome") String nome,
            @Param("status") StatusProjeto status,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(p)
            FROM Projeto p
            JOIN p.membros m
            WHERE m.id = :membroId
              AND p.statusAtual NOT IN :statusInativos
            """)
    long contarProjetosAtivosPorMembro(
            @Param("membroId") Long membroId,
            @Param("statusInativos") Collection<StatusProjeto> statusInativos
    );

    @Query("""
            SELECT COUNT(p)
            FROM Projeto p
            JOIN p.membros m
            WHERE m.id = :membroId
              AND p.id <> :projetoId
              AND p.statusAtual NOT IN :statusInativos
            """)
    long contarProjetosAtivosPorMembroExcetoProjeto(
            @Param("membroId") Long membroId,
            @Param("projetoId") Long projetoId,
            @Param("statusInativos") Collection<StatusProjeto> statusInativos
    );
}