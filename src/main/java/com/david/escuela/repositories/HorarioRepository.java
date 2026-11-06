package com.david.escuela.repositories;

import com.david.escuela.entities.Horario;
import com.david.escuela.enums.DiaSemana;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,Long> {
    boolean existsByGrupoId(Long Id);

    @Query("""
            SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
                    FROM Horario h
                    WHERE h.grupo.id = :idGrupo
                      AND h.diaSemana = :dia
                      AND h.horaInicio < :horaFin
                      AND h.horaFin > :horaInicio
                      AND (:idExcluido IS NULL OR h.id <> :idExcluido)
            """)
    boolean existeTraslapePorGrupo(
            @Param("idGrupo") Long idGrupo,
            @Param("dia") DiaSemana dia,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idExcluido") Long idExcluido
    );

    @Query("""
            SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
                    FROM Horario h
                    WHERE h.grupo.aula.id = :idAula
                      AND h.grupo.periodo = :periodo
                      AND h.grupo.id <> :idGrupo
                      AND h.diaSemana = :dia
                      AND h.horaInicio < :horaFin
                      AND h.horaFin > :horaInicio
                      AND (:idExcluido IS NULL OR h.id <> :idExcluido)
            """)
    boolean existeTraslapePorAula(
            @Param("idAula") Long idAula,
            @Param("periodo") String periodo,
            @Param("idGrupo") Long idGrupo,
            @Param("dia") DiaSemana dia,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("idExcluido") Long idExcluido
    );



}
