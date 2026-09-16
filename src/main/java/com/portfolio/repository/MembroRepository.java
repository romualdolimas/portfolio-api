package com.portfolio.repository;

import com.portfolio.model.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {

    @Query("SELECT m FROM Membro m WHERE m.id = :membroId")
    Optional<Membro> buscarPorId(@Param("membroId") Long membroId);

    @Query("SELECT m FROM Membro m WHERE m.id IN :membroIds")
    List<Membro> buscarTodosPorIds(
            @Param("membroIds") Collection<Long> membroIds
    );
}