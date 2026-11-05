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
        SELECT h.HORA_INICIO || ' - ' || h.HORA_FIN 
        FROM ESCUELA.HORARIOS h 
        INNER JOIN ESCUELA.GRUPOS g ON h.ID_GRUPO = g.ID_GRUPO 
        WHERE (:idGrupo IS NULL OR g.ID_GRUPO = :idGrupo)
          AND (:idAula IS NULL OR g.ID_AULA = :idAula)
          AND (:dia IS NULL OR UPPER(TRIM(h.DIA)) = UPPER(TRIM(:dia)))
          AND (:periodo IS NULL OR g.PERIODO = :periodo)
          AND (:inicio IS NULL AND :fin IS NULL OR (TRIM(h.HORA_INICIO) < :fin AND TRIM(h.HORA_FIN) > :inicio))
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
        SELECT h.HORA_INICIO || ' - ' || h.HORA_FIN 
        FROM ESCUELA.HORARIOS h 
        INNER JOIN ESCUELA.GRUPOS g ON h.ID_GRUPO = g.ID_GRUPO 
        WHERE (:idGrupo IS NULL OR g.ID_GRUPO = :idGrupo)
          AND (:idAula IS NULL OR g.ID_AULA = :idAula)
          AND (:idHorario IS NULL OR h.ID_HORARIO <> :idHorario)
          AND (:dia IS NULL OR UPPER(TRIM(h.DIA)) = UPPER(TRIM(:dia)))
          AND (:periodo IS NULL OR g.PERIODO = :periodo)
          AND (:inicio IS NULL AND :fin IS NULL OR (h.HORA_INICIO < :fin AND h.HORA_FIN > :inicio))
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
