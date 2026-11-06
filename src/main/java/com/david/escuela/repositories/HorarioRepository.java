package com.david.escuela.repositories;

import com.david.escuela.entities.Horario;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,Long> {
    boolean existsByGrupoId(Long Id);
    // para registrar
    @Query(value = """
    SELECT 'CONFLICTO: El grupo ya tiene un horario asignado en ese intervalo.'
    FROM GRUPOS G
    INNER JOIN HORARIOS H
        ON G.ID_GRUPO = H.ID_GRUPO
    WHERE
        G.ID_GRUPO = :idGrupo
        AND G.PERIODO = :periodo
        AND UPPER(TRIM(H.DIA)) = UPPER(TRIM(:dia))
        AND H.HORA_INICIO < :fin
        AND H.HORA_FIN > :inicio

    UNION ALL

    SELECT 'CONFLICTO: El aula ya está ocupada en ese intervalo.'
    FROM GRUPOS G
    INNER JOIN HORARIOS H
        ON G.ID_GRUPO = H.ID_GRUPO
    WHERE
        G.ID_AULA = :idAula
        AND G.PERIODO = :periodo
        AND G.ID_GRUPO <> :idGrupo
        AND UPPER(TRIM(H.DIA)) = UPPER(TRIM(:dia))
        AND H.HORA_INICIO < :fin
        AND H.HORA_FIN > :inicio
    """, nativeQuery = true)
    List<String> buscarHorariosFormateados(
            @Param("inicio") String inicio,
            @Param("fin") String fin,
            @Param("dia") String dia,
            @Param("idGrupo") Long idGrupo,
            @Param("idAula") Long idAula,
            @Param("periodo") String periodo
    );



    //para actualizar
    @Query(value = """
    SELECT 'CONFLICTO: El grupo ya tiene un horario asignado en ese intervalo.'
    FROM GRUPOS G
    INNER JOIN HORARIOS H
        ON G.ID_GRUPO = H.ID_GRUPO
    WHERE
        G.ID_GRUPO = :idGrupo
        AND G.PERIODO = :periodo
        AND UPPER(TRIM(H.DIA)) = UPPER(TRIM(:dia))
        AND H.ID_HORARIO <> :idHorario
        AND H.HORA_INICIO < :fin
        AND H.HORA_FIN > :inicio

    UNION ALL

    SELECT 'CONFLICTO: El aula ya está ocupada en ese intervalo.'
    FROM GRUPOS G
    INNER JOIN HORARIOS H
        ON G.ID_GRUPO = H.ID_GRUPO
    WHERE
        G.ID_AULA = :idAula
        AND G.PERIODO = :periodo
        AND H.ID_HORARIO <> :idHorario
        AND UPPER(TRIM(H.DIA)) = UPPER(TRIM(:dia))
        AND H.HORA_INICIO < :fin
        AND H.HORA_FIN > :inicio
    """, nativeQuery = true)
    List<String> buscarHorariosFormateados(
            @Param("inicio") String inicio,
            @Param("fin") String fin,
            @Param("idGrupo") Long idGrupo,
            @Param("idAula") Long idAula,
            @Param("idHorario") Long idHorario,
            @Param("periodo") String periodo,
            @Param("dia") String dia
    );
}
