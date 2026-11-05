package com.david.escuela.repositories;

import com.david.escuela.entities.Maestro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaestroRepository extends JpaRepository<Maestro,Long> {
    boolean existsByEmailIgnoreCase(String email);



    boolean existsByTelefono(String telefono);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByTelefonoAndId(String telefono, Long id);
}
