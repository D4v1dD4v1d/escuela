package com.david.escuela.repositories;

import com.david.escuela.entities.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion,Long> {
    boolean existsByAlumnoId(Long Id);
    boolean existsByGrupoId(Long Id);
    boolean existsByGrupoIdAndAlumnoId(Long grupoId,Long AlumnoId);
    boolean existsByGrupoIdAndAlumnoIdAndIdNot(Long grupoId,Long AlumnoId,Long id);

}
