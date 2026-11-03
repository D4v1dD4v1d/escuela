package com.david.escuela.repositories;

import com.david.escuela.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoReposiroty extends JpaRepository<Grupo,Long> {

    boolean existsByMaestroId(Long id);
}
