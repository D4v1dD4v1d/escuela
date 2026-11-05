package com.david.escuela.repositories;

import com.david.escuela.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoReposiroty extends JpaRepository<Grupo,Long> {

    boolean existsByMaestroId(Long id);

    boolean existsByAulaId(Long id);

    boolean existsByCursoId(Long id);

    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(Long curso,Long maestro,Long aula, String periodo);

    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(Long curso,Long maestro,Long aula, String periodo, Long id);

}
